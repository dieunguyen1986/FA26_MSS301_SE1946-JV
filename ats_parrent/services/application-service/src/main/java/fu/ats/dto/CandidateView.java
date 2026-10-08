package fu.ats.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateView {
    private Long id;
    private String fullName;
    private String status;
    private String source;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private boolean duplicate;
    private Long activeCvId;
}
