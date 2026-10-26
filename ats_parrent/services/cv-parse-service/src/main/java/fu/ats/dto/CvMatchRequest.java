package fu.ats.dto;

import jakarta.validation.constraints.NotBlank;

public record CvMatchRequest(
        @NotBlank(message = "cvText must not be blank")
        String cvText,
        @NotBlank(message = "jobDescription must not be blank")
        String jobDescription
) {
}
