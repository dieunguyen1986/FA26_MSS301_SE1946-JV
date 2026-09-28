package fu.ats.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Candidate profile returned by the candidate API.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateResponse {

    private Long id;
    private String fullName;
    private String status;
    private String source;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private boolean duplicate;
    private Long activeCvId;
    private ParsedCvDataResponse parsedCvData;
}
