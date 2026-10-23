package az.maliyye.MaliyyeApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SatisQaimesiRepository extends JpaRepository<SatisQaimesi, Long> {
    SatisQaimesi findTopByOrderByIdDesc();
    List<SatisQaimesi> findBySilinibFalseOrderByIdDesc();
    List<SatisQaimesi> findBySilinibTrueOrderByIdDesc();
    List<SatisQaimesi> findBySilinibFalseAndYaradanIstifadeci_UsernameOrderByIdDesc(String username);
    List<SatisQaimesi> findBySilinibTrueAndYaradanIstifadeci_UsernameOrderByIdDesc(String username);
    long countByKontragent_Id(Long id);
}