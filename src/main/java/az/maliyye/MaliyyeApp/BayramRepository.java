package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BayramRepository extends JpaRepository<Bayram, Long> {
    List<Bayram> findByTarixBetween(LocalDate baslangic, LocalDate son);
    boolean existsByTarix(LocalDate tarix);
}