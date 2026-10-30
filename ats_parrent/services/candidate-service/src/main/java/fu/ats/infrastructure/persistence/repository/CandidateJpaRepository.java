package fu.ats.infrastructure.persistence.repository;

import fu.ats.infrastructure.persistence.entity.CandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CandidateJpaRepository extends JpaRepository<CandidateEntity, Long> {

    boolean existsByUserId(UUID userId);

    Optional<CandidateEntity> findByUserId(UUID userId);
}
