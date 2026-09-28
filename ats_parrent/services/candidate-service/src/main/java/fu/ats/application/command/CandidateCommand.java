package fu.ats.application.command;


import java.util.UUID;

public record CandidateCommand(
        String fullName,
        String source,
        String utmSource,
        String utmMedium,
        String utmCampaign,
        UUID userId
) {

}
