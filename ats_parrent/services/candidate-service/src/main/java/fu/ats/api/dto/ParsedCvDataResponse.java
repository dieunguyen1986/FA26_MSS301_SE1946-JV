package fu.ats.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Curated CV data exposed as part of the candidate profile response.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedCvDataResponse {

    private String fullName;
    private String email;
    private String phone;
    private String summary;
    private List<String> skills;
    private List<EducationResponse> education;
    private List<ExperienceResponse> experience;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EducationResponse {
        private String school;
        private String degree;
        private String fieldOfStudy;
        private String startDate;
        private String endDate;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienceResponse {
        private String company;
        private String title;
        private String startDate;
        private String endDate;
        private String description;
    }
}
