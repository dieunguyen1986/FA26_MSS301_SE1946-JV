package fu.ats.domain.repository;

import fu.ats.domain.aggregate.CandidateAggregate;

public interface CandidateRepository {
    CandidateAggregate save(CandidateAggregate aggregate);
}
