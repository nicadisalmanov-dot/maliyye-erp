package az.maliyye.MaliyyeApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface IsciRepository extends JpaRepository<Isci, Long> {
    List<Isci> findByStatusOrderByAdAsc(String status);
    long countByStatus(String status);
}