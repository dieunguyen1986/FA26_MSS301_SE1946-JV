package fu.ats.infrastructure.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Child entity of the Candidate aggregate ("cvs" table).
 * Extends BaseAuditEntity: created_at, updated_at, created_by, updated_by, is_deleted.
 *
 * Consider adding @SQLRestriction("is_deleted = false") (Hibernate 6.3+, or
 * @Where on older versions) at the class level if soft-deleted rows should be
 * transparently excluded from normal queries.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "id")
@Entity
@Table(name = "cvs")
public class CvEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateEntity candidate;

    @Column(name = "file_path", length = 1000)
    private String filePath;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    /**
     * Raw output written back by cv-parser-service after it parses this file
     * (candidate-service does not parse CVs itself — it only persists the
     * result, most likely via an async event/callback from that service).
     * As opposed to CandidateEntity.parsedCvData, which holds the
     * curated/normalized VO derived from this raw payload.
     * No fixed schema was given for this column, so it's kept as a generic
     * map; swap for a typed class once cv-parser-service's output shape is
     * confirmed (e.g. a shared DTO/contract between the two services).
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parsed_data", columnDefinition = "jsonb")
    private Map<String, Object> parsedData;

    /**
     * Set by candidate-service on upload (PENDING), then updated to
     * PARSED/FAILED once cv-parser-service reports back the parse result.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "parse_status", length = 50)
    private ParseStatus parseStatus;

    @Column(name = "uploaded_at", columnDefinition = "timestamptz")
    private OffsetDateTime uploadedAt;
}