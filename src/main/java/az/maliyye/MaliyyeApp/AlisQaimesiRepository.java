package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlisQaimesiRepository extends JpaRepository<AlisQaimesi, Long> {

    // Ən sonuncu qaiməni tapmaq üçün (sənəd nömrəsi üçün)
    AlisQaimesi findTopByOrderByIdDesc();

    // Silinməmiş (aktiv) qaimələri gətirir
    List<AlisQaimesi> findBySilinibFalseOrderByIdDesc();

    // Silinmiş (arxivə köçürülmüş) qaimələri gətirir
    List<AlisQaimesi> findBySilinibTrueOrderByIdDesc();

    // İstifadəçiyə görə aktiv qaimələr (Adi istifadəçi üçün)
    List<AlisQaimesi> findBySilinibFalseAndYaradanIstifadeci_UsernameOrderByIdDesc(String username);

    // İstifadəçiyə görə arxivlənmiş qaimələr
    List<AlisQaimesi> findBySilinibTrueAndYaradanIstifadeci_UsernameOrderByIdDesc(String username);
    long countByKontragent_Id(Long id);
}