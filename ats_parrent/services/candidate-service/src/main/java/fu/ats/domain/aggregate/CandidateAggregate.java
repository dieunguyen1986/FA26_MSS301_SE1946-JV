package fu.ats.domain.aggregate;

import fu.ats.application.command.CandidateCommand;
import fu.ats.infrastructure.persistence.entity.CandidateStatus;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class CandidateAggregate {
    private String fullName;
    private String source;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private CandidateStatus status;
    private UUID userId;

    private CandidateAggregate() {

    }

    public static CandidateAggregate create(CandidateCommand command) {
        List<String> sourceValues = List.of("Facebook", "LinkedIn", "VietnamWorks", "Zalo");
        CandidateAggregate candidateAggregate = new CandidateAggregate();
        candidateAggregate.fullName = command.fullName();
        candidateAggregate.source = command.source();

        if (command.source() == null) {
            throw new IllegalArgumentException("Source is null");
        }
        if (!sourceValues.contains(command.source())) {
            throw new IllegalArgumentException("Source is not valid");
        }

        candidateAggregate.utmSource = command.utmSource();
        candidateAggregate.utmMedium = command.utmMedium();
        candidateAggregate.utmCampaign = command.utmCampaign();
        candidateAggregate.status = CandidateStatus.ACTIVE;
        if(command.userId() == null) {
            throw  new IllegalArgumentException("User id is null");
        }
        candidateAggregate.userId = command.userId();
        return candidateAggregate;
    }
}
