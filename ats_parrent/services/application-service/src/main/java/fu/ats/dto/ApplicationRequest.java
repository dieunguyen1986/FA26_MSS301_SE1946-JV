package fu.ats.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ApplicationRequest(
        @JsonAlias("jobId")
        @NotNull UUID jobId,
        @JsonAlias("candidateId")
        @NotNull Long candidateId,
        @JsonAlias("cvUrl")
        @NotBlank String cvUrl
) {
}