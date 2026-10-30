package fu.ats.application.service;

import fu.ats.api.dto.CandidateResponse;
import fu.ats.api.dto.CandidateUploadRequest;
import fu.ats.api.dto.CvResponse;
import fu.ats.application.port.in.CandidateUploadUseCase;
import fu.ats.application.exception.CandidateAlreadyExistsException;
import fu.ats.application.port.out.CvParseRequestPort;
import fu.ats.application.port.out.CvStoragePort;
import fu.ats.config.CandidateStorageProperties;
import fu.ats.infrastructure.persistence.entity.CandidateEntity;
import fu.ats.infrastructure.persistence.entity.CandidateStatus;
import fu.ats.infrastructure.persistence.entity.CvEntity;
import fu.ats.infrastructure.persistence.entity.ParseStatus;
import fu.ats.infrastructure.persistence.repository.CandidateJpaRepository;
import fu.ats.infrastructure.persistence.repository.CvJpaRepository;
import fu.ats.shared.commonweb.messaging.rabbitmq.CvParseCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateUploadService implements CandidateUploadUseCase {

    private final CandidateJpaRepository candidateJpaRepository;
    private final CvJpaRepository cvJpaRepository;
    private final CvStoragePort cvStoragePort;
    private final CvParseRequestPort cvParseRequestPort;
    private final CandidateStorageProperties storageProperties;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
    );

    @Override
    @Transactional
    public CandidateResponse registerForCurrentUser(
            UUID currentUserId,
            CandidateUploadRequest request
    ) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("Authenticated user is required");
        }
        return createCandidate(currentUserId, request);
    }

    @Override
    @Transactional
    public CandidateResponse createByRecruiter(CandidateUploadRequest request) {
        if (request.getCandidateUserId() == null) {
            throw new IllegalArgumentException(
                    "candidateUserId is required when recruiter creates a candidate"
            );
        }
        return createCandidate(request.getCandidateUserId(), request);
    }

    private CandidateResponse createCandidate(
            UUID candidateUserId,
            CandidateUploadRequest request
    ) {
        if (candidateJpaRepository.existsByUserId(candidateUserId)) {
            throw new CandidateAlreadyExistsException(
                    "Candidate profile already exists for this account"
            );
        }

        MultipartFile file = request.getFile();
        validateFile(file);

        CandidateEntity candidate = CandidateEntity.builder()
                .userId(candidateUserId)
                .fullName(request.getFullName().trim())
                .source(request.getSource().trim())
                .utmSource(request.getUtmSource())
                .utmMedium(request.getUtmMedium())
                .utmCampaign(request.getUtmCampaign())
                .status(CandidateStatus.ACTIVE)
                .build();

        CandidateEntity savedCandidate = candidateJpaRepository.saveAndFlush(candidate);
        String storedFilePath = null;

        try (InputStream content = file.getInputStream()) {
            String contentType = contentType(file);
            storedFilePath = cvStoragePort.upload(
                    savedCandidate.getId(),
                    file.getOriginalFilename(),
                    contentType,
                    file.getSize(),
                    content
            );

            CvEntity cv = CvEntity.builder()
                    .candidate(savedCandidate)
                    .filePath(storedFilePath)
                    .originalFileName(file.getOriginalFilename())
                    .fileType(contentType)
                    .sizeBytes(file.getSize())
                    .parseStatus(ParseStatus.PENDING)
                    .uploadedAt(OffsetDateTime.now(ZoneOffset.UTC))
                    .build();

            // Save the CV directly so that JPA returns the generated CV ID.
            CvEntity savedCv = cvJpaRepository.saveAndFlush(cv);

            savedCandidate.setCvFileId(savedCv.getId());
            candidateJpaRepository.saveAndFlush(savedCandidate);

            CvParseCommand command = CvParseCommand.create(
                    UUID.randomUUID().toString(),
                    savedCv.getId(),
                    savedCandidate.getId(),
                    storedFilePath,
                    contentType
            );
            cvParseRequestPort.requestParsing(command);


            return toResponse(savedCandidate, savedCv);
        } catch (IOException exception) {
            deleteStoredFileQuietly(storedFilePath);
            throw new IllegalStateException("Cannot read uploaded CV", exception);
        } catch (RuntimeException exception) {
            deleteStoredFileQuietly(storedFilePath);
            throw exception;
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("CV file must not be empty");
        }
        if (file.getSize() > storageProperties.maxFileSize().toBytes()) {
            throw new IllegalArgumentException("CV file is too large");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(contentType(file))) {
            throw new IllegalArgumentException(
                    "Only PDF, DOC, DOCX and TXT CV files are supported"
            );
        }
    }

    private String contentType(MultipartFile file) {
        return file.getContentType() == null
                ? "application/octet-stream"
                : file.getContentType();
    }

    private void deleteStoredFileQuietly(String filePath) {
        if (filePath == null) {
            return;
        }
        try {
            cvStoragePort.delete(filePath);
        } catch (RuntimeException cleanupException) {
            log.warn("Cannot clean up uploaded CV object {}", filePath, cleanupException);
        }
    }

    private CandidateResponse toResponse(CandidateEntity candidate, CvEntity cv) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .fullName(candidate.getFullName())
                .status(candidate.getStatus().name())
                .source(candidate.getSource())
                .utmSource(candidate.getUtmSource())
                .utmMedium(candidate.getUtmMedium())
                .utmCampaign(candidate.getUtmCampaign())
                .duplicate(candidate.isDuplicate())
                .activeCvId(candidate.getCvFileId())
                .activeCv(toCvResponse(candidate, cv))
                .build();
    }

    private CvResponse toCvResponse(CandidateEntity candidate, CvEntity cv) {
        return CvResponse.builder()
                .cvId(cv.getId())
                .originalFileName(cv.getOriginalFileName())
                .fileType(cv.getFileType())
                .sizeBytes(cv.getSizeBytes())
                .parseStatus(cv.getParseStatus().name())
                .uploadedAt(cv.getUploadedAt())
                .active(cv.getId().equals(candidate.getCvFileId()))
                .build();
    }
}
