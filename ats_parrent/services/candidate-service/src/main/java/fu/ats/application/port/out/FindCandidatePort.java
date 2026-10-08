package fu.ats.application.port.out;

import fu.ats.application.query.CandidateQuery;
import fu.ats.domain.aggregate.CandidateAggregate;

import java.util.Optional;

public interface FindCandidatePort {
    Optional<CandidateAggregate> findById(Long id);
}
