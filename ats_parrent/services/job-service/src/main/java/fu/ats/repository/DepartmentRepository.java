package fu.ats.repository;

import fu.ats.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
}
