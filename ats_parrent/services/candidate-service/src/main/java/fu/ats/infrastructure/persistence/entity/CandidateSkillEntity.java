package fu.ats.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Child entity of the Candidate aggregate. Enforces UNIQUE(candidate_id, skill_id).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(
        name = "candidate_skills",
        uniqueConstraints = @UniqueConstraint(
                name = "ux_candidate_skills_candidate_skill",
                columnNames = {"candidate_id", "skill_id"}
        )
)
public class CandidateSkillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateEntity candidate;

    /**
     * Cross-service reference to job-service.skills.id. Kept as a plain Long
     * (no JPA relation / FK constraint) since that table lives in a different
     * microservice's database.
     */
    @Column(name = "skill_id", nullable = false)
    private Long skillId;

    @Column(name = "level", length = 20)
    private String level;

    @Column(name = "years_exp")
    private Integer yearsExp;
}