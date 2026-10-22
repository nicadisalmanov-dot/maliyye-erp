package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LayiheRepository extends JpaRepository<Layihe, Long> {

    // 1. İstifadəçilərin bu layihə ilə əlaqəsini silirik (user_projects cədvəli)
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_projects WHERE project_id = :id", nativeQuery = true)
    void istifadeciLayiheEloqeleriniSil(@Param("id") Long id);

    // 2. Anbarların bu layihə ilə əlaqəsini silirik (anbar_layiheler cədvəli)
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM anbar_layiheler WHERE layihe_id = :id", nativeQuery = true)
    void anbarLayiheEloqeleriniSil(@Param("id") Long id);
    // İstifadəçilərin bu layihəyə bağlılığının sayını qaytarır
    @Query(value = "SELECT COUNT(*) FROM user_projects WHERE project_id = :id", nativeQuery = true)
    long istifadeciLayiheEloqesiSayi(@Param("id") Long id);

    // Anbarların bu layihəyə bağlılığının sayını qaytarır
    @Query(value = "SELECT COUNT(*) FROM anbar_layiheler WHERE layihe_id = :id", nativeQuery = true)
    long anbarLayiheEloqesiSayi(@Param("id") Long id);
}