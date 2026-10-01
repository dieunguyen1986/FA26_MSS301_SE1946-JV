package fu.ats.domain.repository;

import fu.ats.domain.aggregate.CandidateAggregate;

import java.util.Optional;
import java.util.UUID;

public interface CandidateRepository {
    Optional<CandidateAggregate> findByUserId(UUID userId);
}
