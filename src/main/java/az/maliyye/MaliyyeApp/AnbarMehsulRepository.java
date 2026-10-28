package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnbarMehsulRepository extends JpaRepository<AnbarMehsul, Long> {
    // Anbara görə malları tap
    List<AnbarMehsul> findByAnbar_Id(Long anbarId);

    // Mal və Anbara görə tap (əgər varsa, üzərinə əlavə etmək üçün)
    AnbarMehsul findByAnbar_IdAndMehsul_Id(Long anbarId, Long mehsulId);
}