package fu.ats.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root. Owns CvEntity and CandidateSkillEntity as children
 * (cascade ALL + orphanRemoval so the aggregate manages its own lifecycle).
 *
 * This is a pure persistence/infrastructure entity — keep domain logic in the
 * domain layer and map to/from a domain Candidate model via a mapper, per
 * Clean Architecture.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(
        name = "candidates",
        indexes = @Index(name = "ux_candidates_user_id", columnList = "user_id", unique = true)
)
public class CandidateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Keycloak sub. */
    @Column(name = "user_id", nullable = false, unique = true, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "status", length = 50)
    @Enumerated(EnumType.STRING)
    private CandidateStatus status;

    @Column(name = "source", length = 150)
    private String source;

    @Column(name = "utm_source", length = 150)
    private String utmSource;

    @Column(name = "utm_medium", length = 150)
    private String utmMedium;

    @Column(name = "utm_campaign", length = 255)
    private String utmCampaign;

    @Column(name = "is_duplicate", nullable = false)
    @Builder.Default
    private boolean duplicate = false;

    /**
     * Points to the candidate's "current/active" CV (cvs.id).
     * Kept as a plain FK column (Long), NOT a JPA @OneToOne, because the
     * owning side of the Candidate 1---N Cv relationship is already
     * Cv.candidate_id below. Mapping this as an object reference too would
     * create a second, conflicting association path between the same tables.
     */
    @Column(name = "cv_file_id")
    private Long cvFileId;

    /**
     * Curated/normalized parse result, derived from the raw payload
     * cv-parser-service writes into CvEntity.parsedData for the active CV.
     * candidate-service does not parse CVs itself — it only consumes and
     * reshapes the result (e.g. via an event listener/callback handler).
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parsed_cv_data", columnDefinition = "jsonb")
    private ParsedCvData parsedCvData;

    @Builder.Default
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CvEntity> cvs = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateSkillEntity> skills = new ArrayList<>();
}