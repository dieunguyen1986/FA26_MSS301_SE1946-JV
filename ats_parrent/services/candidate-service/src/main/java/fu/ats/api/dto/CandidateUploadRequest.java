package fu.ats.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Form data used when a candidate profile and its CV are created together.
 * candidateUserId is filled only by recruiter/admin requests.
 */
@Getter
@Setter
@NoArgsConstructor
public class CandidateUploadRequest {

    @NotBlank(message = "Full name must not be blank")
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;

    @NotBlank(message = "Source must not be blank")
    @Size(max = 150, message = "Source must not exceed 150 characters")
    private String source;

    @Size(max = 150, message = "UTM source must not exceed 150 characters")
    private String utmSource;

    @Size(max = 150, message = "UTM medium must not exceed 150 characters")
    private String utmMedium;

    @Size(max = 255, message = "UTM campaign must not exceed 255 characters")
    private String utmCampaign;

    /** Keycloak subject of the candidate; used only by recruiter/admin. */
    private UUID candidateUserId;

    /** Multipart form field name: file. */
    private MultipartFile file;
}
