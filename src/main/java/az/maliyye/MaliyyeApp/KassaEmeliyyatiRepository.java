package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface KassaEmeliyyatiRepository extends JpaRepository<KassaEmeliyyati, Long> {

    // Axtarış (təsvirə və ya sənəd nömrəsinə görə)
    List<KassaEmeliyyati> findByTesvirContainingIgnoreCaseOrSenedNomresiContainingIgnoreCase(String tesvir, String senedNomresi);

    // Ay üzrə filter (tarix aralığı ilə)
    List<KassaEmeliyyati> findByTarixBetween(LocalDate baslangic, LocalDate son);

    // Növ üzrə filter (MƏDAXİL / MƏXARİC)
    List<KassaEmeliyyati> findByNov(String nov);

    // Həm tarix, həm növ üzrə
    List<KassaEmeliyyati> findByTarixBetweenAndNov(LocalDate baslangic, LocalDate son, String nov);
}