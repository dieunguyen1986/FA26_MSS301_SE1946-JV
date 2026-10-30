package fu.ats.api.rest;

import fu.ats.api.dto.CandidateRequest;
import fu.ats.api.dto.CandidateResponse;
import fu.ats.api.dto.CandidateUploadRequest;
import fu.ats.application.command.CandidateCommand;
import fu.ats.application.port.in.CandidateUploadUseCase;
import fu.ats.application.port.in.CreateCandidateUseCase;
import fu.ats.application.port.in.FindCandidateUseCase;
import fu.ats.application.query.CandidateQuery;
import fu.ats.security.GatewayUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/candidates")
@RequiredArgsConstructor
public class CandidateController {
    private final CreateCandidateUseCase createCandidatePort;
    private final FindCandidateUseCase findCandidateUseCase;
    private final CandidateUploadUseCase candidateUploadUseCase;

    @PostMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('CANDIATE', 'ADMIN')")
    public ResponseEntity<CandidateResponse> registerCandidate(
            @Valid @ModelAttribute CandidateUploadRequest request,
            @AuthenticationPrincipal GatewayUser user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                candidateUploadUseCase.registerForCurrentUser(
                        parseUserId(user),
                        request
                )
        );
    }

    @PostMapping(value = "/recruiter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<CandidateResponse> createCandidateByRecruiter(
            @Valid @ModelAttribute CandidateUploadRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                candidateUploadUseCase.createByRecruiter(request)
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CANDIATE', 'ADMIN')")
    public ResponseEntity<CandidateResponse> createCandidate(
            @Valid @RequestBody CandidateRequest request,
            @AuthenticationPrincipal GatewayUser user
    ) {

        createCandidatePort.execute(new CandidateCommand(
                request.getFullName(),
                request.getSource(),
                request.getUtmSource(),
                request.getUtmMedium(),
                request.getUtmCampaign(),
                java.util.UUID.fromString(user.subject())
        ));

        return ResponseEntity.ok(new CandidateResponse());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@candidateAuthorization.canRead(#id, authentication)")
    public ResponseEntity<CandidateResponse> getCandidateById(@PathVariable("id") Long id) {

        return ResponseEntity.ok(findCandidateUseCase.execute(new CandidateQuery(id)));
    }

    private java.util.UUID parseUserId(GatewayUser user) {
        if (user == null || user.subject() == null || user.subject().isBlank()) {
            throw new IllegalArgumentException("Authenticated user is required");
        }
        try {
            return java.util.UUID.fromString(user.subject());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Authenticated user id must be a UUID");
        }
    }
}
