package fu.ats.controller;

import fu.ats.dto.ApplicationRequest;
import fu.ats.dto.ApplicationResponse;
import fu.ats.service.ApplicationServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {
    private final ApplicationServiceImpl applicationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CANDIATE', 'ADMIN')")
    public ResponseEntity<ApplicationResponse> apply(@Valid @RequestBody ApplicationRequest request,
                                                     Authentication authentication
    ) {

        log.info("Application created by {}", authentication.getName());

        ApplicationResponse response = applicationService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/applications/" + response.id())).body(response);
    }
}
