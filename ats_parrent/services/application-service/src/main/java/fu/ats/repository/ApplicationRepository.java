package fu.ats.repository;

import fu.ats.entity.Application;
import fu.ats.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByJobIdAndCandidateIdAndStatusNot(UUID jobId, Long candidateId, ApplicationStatus status);
}