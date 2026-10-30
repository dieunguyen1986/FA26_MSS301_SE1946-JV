package fu.ats.api.dto;

import lombok.Builder;

import java.time.OffsetDateTime;

/** Public CV metadata. The internal MinIO path is intentionally not exposed. */
@Builder
public record CvResponse(
        Long cvId,
        String originalFileName,
        String fileType,
        Long sizeBytes,
        String parseStatus,
        OffsetDateTime uploadedAt,
        boolean active
) {
}
