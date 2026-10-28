package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlisQaimesiRepository extends JpaRepository<AlisQaimesi, Long> {

    AlisQaimesi findTopByOrderByIdDesc();
    List<AlisQaimesi> findBySilinibFalseOrderByIdDesc();
    List<AlisQaimesi> findBySilinibTrueOrderByIdDesc();
    List<AlisQaimesi> findBySilinibFalseAndYaradanIstifadeci_UsernameOrderByIdDesc(String username);
    List<AlisQaimesi> findBySilinibTrueAndYaradanIstifadeci_UsernameOrderByIdDesc(String username);
    long countByKontragent_Id(Long id);
    List<AlisQaimesi> findBySilinibFalseAndKontragent_Id(Long kontragentId);

    // DÜZƏLDİLDİ: Cədvəl adı 'alis_qaimesi_setirleri' olaraq düzgün yazıldı
    @Modifying
    @Query(value = "DELETE FROM alis_qaimesi_setirleri WHERE alis_qaimesi_id = :id", nativeQuery = true)
    void deleteSetirlerByQaimeId(@Param("id") Long id);

    @Modifying
    @Query(value = "DELETE FROM alis_qaimeleri WHERE id = :id", nativeQuery = true)
    void deleteQaimeById(@Param("id") Long id);
}