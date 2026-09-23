package fu.ats.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "department_name", nullable = false, length = 255)
    private String departmentName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "manager_id")
    private Long managerId;

    @OneToMany(mappedBy = "department")
    private List<Job> jobs = new ArrayList<>();

}