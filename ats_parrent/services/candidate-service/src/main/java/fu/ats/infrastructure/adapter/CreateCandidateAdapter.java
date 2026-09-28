package fu.ats.infrastructure.adapter;

import fu.ats.application.port.out.SaveCandidatePort;
import fu.ats.domain.aggregate.CandidateAggregate;
import fu.ats.infrastructure.persistence.entity.CandidateEntity;
import fu.ats.infrastructure.persistence.entity.CandidateStatus;
import fu.ats.infrastructure.persistence.repository.CandidateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateCandidateAdapter implements SaveCandidatePort {
    private final CandidateJpaRepository candidateJpaRepository;
    @Override
    public CandidateAggregate save(CandidateAggregate aggregate) {
        // map aggregate to Entity

        CandidateEntity candidateEntity = CandidateEntity.builder()
                .fullName(aggregate.getFullName())
                .source(aggregate.getSource())
                .utmMedium(aggregate.getUtmMedium())
                .utmCampaign(aggregate.getUtmCampaign())
                .status(aggregate.getStatus())
                .userId(aggregate.getUserId())
                .build();

        candidateJpaRepository.save(candidateEntity);

        return aggregate;
    }
}
