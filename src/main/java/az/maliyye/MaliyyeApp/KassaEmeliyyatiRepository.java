package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface KassaEmeliyyatiRepository extends JpaRepository<KassaEmeliyyati, Long> {

    List<KassaEmeliyyati> findByTesvirContainingIgnoreCaseOrSenedNomresiContainingIgnoreCase(String tesvir, String senedNomresi);

    List<KassaEmeliyyati> findByTarixBetween(LocalDate baslangic, LocalDate son);

    List<KassaEmeliyyati> findByTesdiqlenibFalseOrderByIdDesc();

    List<KassaEmeliyyati> findByTesdiqlenibTrueOrderByIdDesc();

    List<KassaEmeliyyati> findByYaradanIstifadeci_UsernameOrderByIdDesc(String username);

    long countByLayihe_Id(Long id);

    long countByKassaHesabi_Id(Long id);

    long countByKontragent_Id(Long id);
}