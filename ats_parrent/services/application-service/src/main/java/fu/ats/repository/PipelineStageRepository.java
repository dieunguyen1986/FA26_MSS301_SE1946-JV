package fu.ats.repository;

import fu.ats.entity.PipelineStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PipelineStageRepository extends JpaRepository<PipelineStage, Long> {

    Optional<PipelineStage> findFirstByDefaultStageTrueOrderByStageOrderAsc();

    PipelineStage findByDefaultStage(boolean defaultStage);
}