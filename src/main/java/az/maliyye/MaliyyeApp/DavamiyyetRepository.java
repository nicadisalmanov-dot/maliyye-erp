package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DavamiyyetRepository extends JpaRepository<Davamiyyet, Long> {
    List<Davamiyyet> findByIsci_IdAndTarixBetween(Long isciId, LocalDate baslangic, LocalDate son);
    List<Davamiyyet> findByTarixBetween(LocalDate baslangic, LocalDate son);
    Davamiyyet findByIsci_IdAndTarix(Long isciId, LocalDate tarix);
}