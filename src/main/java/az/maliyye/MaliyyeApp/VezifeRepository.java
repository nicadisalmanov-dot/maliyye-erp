package az.maliyye.MaliyyeApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface VezifeRepository extends JpaRepository<Vezife, Long> {}