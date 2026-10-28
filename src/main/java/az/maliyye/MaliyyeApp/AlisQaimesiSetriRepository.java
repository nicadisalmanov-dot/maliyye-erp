package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlisQaimesiSetriRepository extends JpaRepository<AlisQaimesiSetri, Long> {

    // YENİ: Bu məhsul hər hansı bir alış qaiməsində istifadə olunubmu?
    boolean existsByMehsul_Id(Long mehsulId);
}