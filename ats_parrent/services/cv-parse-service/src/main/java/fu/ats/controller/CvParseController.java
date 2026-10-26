package fu.ats.controller;

import fu.ats.dto.CvAnalysisRequest;
import fu.ats.dto.CvAnalysisResponse;
import fu.ats.dto.CvMatchRequest;
import fu.ats.dto.CvMatchResponse;
import fu.ats.service.CvParseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cvparse")
@RequiredArgsConstructor
public class CvParseController {

    private final CvParseService cvParseService;

    @PostMapping("/analyze")
    @PreAuthorize("hasAnyRole('CANDIATE', 'RECRUITER', 'ADMIN')")
    public CvAnalysisResponse analyze(
            @Valid @RequestBody CvAnalysisRequest request
    ) {
        return cvParseService.analyze(request);
    }

    @PostMapping("/match")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public CvMatchResponse match(
            @Valid @RequestBody CvMatchRequest request
    ) {
        return cvParseService.match(request);
    }
}
