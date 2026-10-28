package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SatisQaimesiSetriRepository extends JpaRepository<SatisQaimesiSetri, Long> {

    // YENİ: Bu məhsul hər hansı bir satış qaiməsində istifadə olunubmu?
    boolean existsByMehsul_Id(Long mehsulId);
}