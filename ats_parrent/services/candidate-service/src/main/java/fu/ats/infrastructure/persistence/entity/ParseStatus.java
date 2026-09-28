package fu.ats.infrastructure.persistence.entity;


/**
 * Maps cvs.parse_status (varchar(50)), stored as STRING via @Enumerated.
 */
public enum ParseStatus {
    PENDING,
    PARSED,
    FAILED
}