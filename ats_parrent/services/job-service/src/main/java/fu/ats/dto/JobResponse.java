package fu.ats.dto;


import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter@Setter
@NoArgsConstructor@AllArgsConstructor
@Builder
public class JobResponse {
    private UUID id;
    private String title;

    private String description;

    private String location;
    private String status;

    private OffsetDateTime deadline;
}
