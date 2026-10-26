package fu.ats.service;

import fu.ats.dto.CvAnalysisRequest;
import fu.ats.dto.CvAnalysisResponse;
import fu.ats.dto.CvMatchRequest;
import fu.ats.dto.CvMatchResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CvParseServiceImpl implements CvParseService {

    private static final List<String> KNOWN_SKILLS = List.of(
            "Java", "Spring Boot", "Spring Cloud", "React", "JavaScript",
            "TypeScript", "Python", "SQL", "PostgreSQL", "Docker",
            "Kubernetes", "AWS", "Git", "Kafka", "Redis"
    );

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", Pattern.CASE_INSENSITIVE);

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("(?:\\+?\\d[\\d .()-]{7,}\\d)");

    private static final Pattern EXPERIENCE_PATTERN =
            Pattern.compile("(\\d+)\\s*\\+?\\s*(?:years?|năm)", Pattern.CASE_INSENSITIVE);

    @Override
    public CvAnalysisResponse analyze(CvAnalysisRequest request) {
        String text = request.cvText();
        String normalizedText = text.toLowerCase(Locale.ROOT);

        return new CvAnalysisResponse(
                request.candidateId(),
                request.fileName(),
                findFirst(EMAIL_PATTERN, text),
                findFirst(PHONE_PATTERN, text),
                findSkills(normalizedText),
                findLanguages(normalizedText),
                findEducation(normalizedText),
                findYearsOfExperience(text),
                "Simulated analysis found skills and contact information from the CV text.",
                "SIMULATED"
        );
    }

    @Override
    public CvMatchResponse match(CvMatchRequest request) {
        String cvText = request.cvText().toLowerCase(Locale.ROOT);
        String jobDescription = request.jobDescription().toLowerCase(Locale.ROOT);

        List<String> requiredSkills = findSkills(jobDescription);
        List<String> matchedSkills = requiredSkills.stream()
                .filter(skill -> cvText.contains(skill.toLowerCase(Locale.ROOT)))
                .toList();
        List<String> missingSkills = requiredSkills.stream()
                .filter(skill -> !matchedSkills.contains(skill))
                .toList();

        int score = requiredSkills.isEmpty()
                ? 0
                : matchedSkills.size() * 100 / requiredSkills.size();

        String recommendation = score >= 70
                ? "Good match for initial screening"
                : "Review manually before moving to the next stage";

        return new CvMatchResponse(score, matchedSkills, missingSkills, recommendation);
    }

    private List<String> findSkills(String normalizedText) {
        return KNOWN_SKILLS.stream()
                .filter(skill -> normalizedText.contains(skill.toLowerCase(Locale.ROOT)))
                .toList();
    }

    private List<String> findLanguages(String normalizedText) {
        return List.of("english", "vietnamese", "japanese", "korean")
                .stream()
                .filter(normalizedText::contains)
                .map(language -> language.substring(0, 1).toUpperCase() + language.substring(1))
                .toList();
    }

    private String findEducation(String normalizedText) {
        if (normalizedText.contains("master") || normalizedText.contains("thạc sĩ")) {
            return "Master degree detected";
        }
        if (normalizedText.contains("bachelor")
                || normalizedText.contains("university")
                || normalizedText.contains("đại học")) {
            return "Bachelor degree or university education detected";
        }
        return "Not detected";
    }

    private int findYearsOfExperience(String text) {
        Matcher matcher = EXPERIENCE_PATTERN.matcher(text);
        int years = 0;

        while (matcher.find()) {
            years = Math.max(years, Integer.parseInt(matcher.group(1)));
        }

        return years;
    }

    private String findFirst(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group() : null;
    }
}
