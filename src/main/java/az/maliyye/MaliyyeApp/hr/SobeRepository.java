package az.maliyye.MaliyyeApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SobeRepository extends JpaRepository<Sobe, Long> {
    long countByRehber_Id(Long isciId);
}