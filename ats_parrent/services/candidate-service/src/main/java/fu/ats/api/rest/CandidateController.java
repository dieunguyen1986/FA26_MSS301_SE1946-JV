package fu.ats.api.rest;

import fu.ats.api.dto.CandidateRequest;
import fu.ats.api.dto.CandidateResponse;
import fu.ats.application.command.CandidateCommand;
import fu.ats.application.port.in.CreateCandidateUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/candidates")
@RequiredArgsConstructor
public class CandidateController {
    private final CreateCandidateUseCase createCandidatePort;

    @PostMapping
    public ResponseEntity<CandidateResponse> createCandidate(@Valid @RequestBody CandidateRequest request) {

        createCandidatePort.execute(new CandidateCommand(request.getFullName(), request.getSource(), request.getUtmSource(), request.getUtmMedium(), request.getUtmCampaign(), request.getUserId()));

        return ResponseEntity.ok(new CandidateResponse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateResponse> getCandidateById(@PathVariable("id") UUID id) {

        return ResponseEntity.ok(new CandidateResponse());
    }
}
