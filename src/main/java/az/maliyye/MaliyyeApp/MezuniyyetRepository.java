package az.maliyye.MaliyyeApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface MezuniyyetRepository extends JpaRepository<Mezuniyyet, Long> {
    List<Mezuniyyet> findByStatusOrderByBaslamaTarixiDesc(String status);
}