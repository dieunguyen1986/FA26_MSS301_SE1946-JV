package fu.ats.infrastructure.persistence.repository;

import fu.ats.infrastructure.persistence.entity.CvEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository for CV records. */
@Repository
public interface CvJpaRepository extends JpaRepository<CvEntity, Long> {
}
