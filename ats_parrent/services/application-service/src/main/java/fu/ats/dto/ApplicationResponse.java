package fu.ats.dto;

import fu.ats.entity.Application;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ApplicationResponse(
        Long id,
        UUID jobId,
        Long candidateId,
        String cvUrl,
        String status,
        OffsetDateTime appliedAt
) {
    public static ApplicationResponse from(Application application) {
        return new ApplicationResponse(
                application.getId(),
                application.getJobId(),
                application.getCandidateId(),
                application.getCvUrl(),
                application.getStatus().name(),
                application.getAppliedAt());
    }
}