package fu.ats.dto;

import java.util.List;

public record CvMatchResponse(
        int score,
        List<String> matchedSkills,
        List<String> missingSkills,
        String recommendation
) {
}
