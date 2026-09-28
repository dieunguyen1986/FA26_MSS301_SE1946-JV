package fu.ats.infrastructure.persistence.repository;

import fu.ats.domain.aggregate.CandidateAggregate;
import fu.ats.infrastructure.persistence.entity.CandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateJpaRepository extends JpaRepository<CandidateEntity, Long> {
}
