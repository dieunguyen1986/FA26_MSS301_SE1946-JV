package fu.ats.dto;

import jakarta.validation.constraints.NotBlank;

public record CvAnalysisRequest(
        Long candidateId,
        String fileName,
        @NotBlank(message = "cvText must not be blank")
        String cvText
) {
}
