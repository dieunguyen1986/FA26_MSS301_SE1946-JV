package fu.ats.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRequest {
    private UUID id;
    private Long recruiterId;

    @NotBlank(message = "Title is not blank")
    private String title;

    private String description;

    private String location;

    @Min(value = 0, message = "Min sal must be >0")
    private BigDecimal salaryMin;

    @Min(value = 0, message = "Max sal must be >0")
    private BigDecimal salaryMax;

    private String status;

    private String utmSource;

    private String utmMedium;

    private OffsetDateTime deadline;

    private OffsetDateTime publishedAt;
    private UUID departmentId;
}
