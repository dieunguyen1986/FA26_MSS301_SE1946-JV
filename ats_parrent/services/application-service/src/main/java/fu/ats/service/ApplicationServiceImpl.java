package fu.ats.service;

import fu.ats.client.candidate.CandidateClient;
import fu.ats.client.job.JobClient;
import fu.ats.dto.ApplicationRequest;
import fu.ats.dto.ApplicationResponse;
import fu.ats.dto.CandidateView;
import fu.ats.dto.JobView;
import fu.ats.entity.Application;
import fu.ats.entity.ApplicationStatus;
import fu.ats.entity.PipelineStage;
import fu.ats.exception.BusinessException;
import fu.ats.repository.ApplicationRepository;
import fu.ats.repository.PipelineStageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final PipelineStageRepository pipelineStageRepository;
    private final JobClient jobClient;
    private final CandidateClient candidateClient;

    @Transactional
    public ApplicationResponse create(ApplicationRequest request) {
        // Call job-service

        JobView jobView = jobClient.findById(request.jobId());

        log.info("Creating application with id {}", jobView);

        if (!"PUBLISHED".equals(jobView.getStatus()) || jobView.getDeadline().isBefore(OffsetDateTime.now())) {
            throw new BusinessException(1, "Job is not published or over due!");
        }

        // Call candidate

        CandidateView candidateView = candidateClient.getCandidateById(request.candidateId());

        if ("INACTIVE".equals(candidateView.getStatus())) {
            throw new BusinessException(2, "JCandidate is not valid to appy this job!");
        }

        if (applicationRepository.existsByJobIdAndCandidateIdAndStatusNot(
                request.jobId(), request.candidateId(), ApplicationStatus.WITHDRAWN)) {
            throw new IllegalStateException("Candidate already has an active application for this job");
        }


        Application application = new Application();
        application.setJobId(request.jobId());
        application.setCandidateId(request.candidateId());
        application.setCvUrl(request.cvUrl());
        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setAppliedAt(OffsetDateTime.now());
        application.setPipelineStage(pipelineStageRepository.findByDefaultStage(true));

        return ApplicationResponse.from(applicationRepository.save(application));
    }

    private PipelineStage getDefaultStage() {
        return pipelineStageRepository.findFirstByDefaultStageTrueOrderByStageOrderAsc()
                .orElseGet(() -> {
                    PipelineStage stage = new PipelineStage();
                    stage.setStageName("Applied");
                    stage.setStageOrder(1);
                    stage.setDefaultStage(true);
                    return pipelineStageRepository.save(stage);
                });
    }
}