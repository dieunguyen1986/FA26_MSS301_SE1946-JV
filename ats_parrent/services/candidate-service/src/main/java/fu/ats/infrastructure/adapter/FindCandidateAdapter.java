package fu.ats.infrastructure.adapter;

import fu.ats.application.port.out.FindCandidatePort;
import fu.ats.application.query.CandidateQuery;
import fu.ats.domain.aggregate.CandidateAggregate;
import fu.ats.infrastructure.persistence.entity.CandidateEntity;
import fu.ats.infrastructure.persistence.repository.CandidateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.management.Query;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FindCandidateAdapter implements FindCandidatePort {
    private final CandidateJpaRepository candidateJpaRepository;

    @Override
    public Optional<CandidateAggregate> findById(Long id) {
        return Optional.of(toAggregate(candidateJpaRepository.findById(id).orElseThrow(() -> {
            throw new IllegalArgumentException("Candidate not found");
        })));
    }

    private CandidateAggregate toAggregate(CandidateEntity candidate) {
        CandidateAggregate aggregate = CandidateAggregate.restore(candidate.getFullName(),
                candidate.getSource(), candidate.getUtmSource(), candidate.getUtmMedium(),

                candidate.getUtmCampaign(), candidate.getStatus());

        return aggregate;
    }
}
