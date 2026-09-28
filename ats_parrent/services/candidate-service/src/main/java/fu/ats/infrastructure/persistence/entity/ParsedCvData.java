package fu.ats.infrastructure.persistence.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedCvData {

    private String fullName;
    private String email;
    private String phone;
    private String summary;
    private List<String> skills;
    private List<Education> education;
    private List<Experience> experience;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Education {
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
    public static class Experience {
        private String company;
        private String title;
        private String startDate;
        private String endDate;
        private String description;
    }
}