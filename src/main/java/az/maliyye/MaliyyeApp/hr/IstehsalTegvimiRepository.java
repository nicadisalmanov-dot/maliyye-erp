package az.maliyye.MaliyyeApp.hr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IstehsalTegvimiRepository extends JpaRepository<IstehsalTegvimi, Long> {
    Optional<IstehsalTegvimi> findByTarix(LocalDate tarix);
    List<IstehsalTegvimi> findByTarixBetween(LocalDate baslangic, LocalDate son);
    boolean existsByTarix(LocalDate tarix);
    void deleteByTarixBetween(LocalDate baslangic, LocalDate son);
}