package fu.ats.api.rest;

import fu.ats.api.dto.CandidateRequest;
import fu.ats.api.dto.CandidateResponse;
import fu.ats.application.command.CandidateCommand;
import fu.ats.application.port.in.CreateCandidateUseCase;
import fu.ats.application.port.in.FindCandidateUseCase;
import fu.ats.application.query.CandidateQuery;
import fu.ats.security.GatewayUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/candidates")
@RequiredArgsConstructor
public class CandidateController {
    private final CreateCandidateUseCase createCandidatePort;
    private final FindCandidateUseCase findCandidateUseCase;

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
}
