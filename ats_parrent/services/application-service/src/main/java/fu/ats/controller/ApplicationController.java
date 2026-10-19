package fu.ats.controller;

import fu.ats.dto.ApplicationRequest;
import fu.ats.dto.ApplicationResponse;
import fu.ats.service.ApplicationServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {
    private final ApplicationServiceImpl applicationService;

    @PostMapping
    public ResponseEntity<ApplicationResponse> apply(@Valid @RequestBody ApplicationRequest request,
                                                     @RequestHeader("X-User-ID") String userId,
                                                     @RequestHeader("X-User-Email") String email,
                                                     @RequestHeader("X-User-Name") String userName
    ) {

        log.info("User Id {}, User email {}, name {}", userId, email, userName);

        ApplicationResponse response = applicationService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/applications/" + response.id())).body(response);
    }
}