package fu.ats.controller;

import fu.ats.dto.JobRequest;
import fu.ats.dto.JobResponse;
import fu.ats.dto.ResponseApi;
import fu.ats.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Slf4j
public class JobController {
    private final JobService jobService;

    @PostMapping
    public ResponseEntity<ResponseApi> create(@Valid @RequestBody JobRequest request,
                                              @RequestHeader("X-User-ID") String userId,
                                              @RequestHeader("X-User-Email") String email,
                                              @RequestHeader("X-User-Name") String userName
                                              ) {
        log.info("User Id {}, User email {}, name {}", userId, email, userName);

        JobResponse jobResponse = jobService.save(request);

        return ResponseEntity.ok(ResponseApi.builder()
                .message("Job created")
                .code(HttpStatus.CREATED)
                 .data(jobResponse)
                .build());
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<ResponseApi> read(@Valid @PathVariable(name = "id") UUID uuid) {
//        return ResponseEntity.ok(ResponseApi.builder().code(HttpStatus.ACCEPTED).message("")
//                .data(jobService.findById(uuid)).build());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> read(@Valid @PathVariable(name = "id") UUID uuid,
                                            @RequestHeader("X-User-ID") String userId,
                                            @RequestHeader("X-User-Email") String email,
                                            @RequestHeader("X-User-Name") String userName
                                            ) {

        log.info("User Id {}, User email {}, name {}", userId, email, userName);

        return ResponseEntity.ok(jobService.findById(uuid));
    }
}
