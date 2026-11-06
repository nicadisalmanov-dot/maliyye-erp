package az.maliyye.MaliyyeApp.kontragent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KontragentRepository extends JpaRepository<Kontragent, Long> {
    List<Kontragent> findByKontragentNovu(String kontragentNovu);
}