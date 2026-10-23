package az.maliyye.MaliyyeApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XidmetRepository extends JpaRepository<Xidmet, Long> {
    // Müəyyən bir qrupda olan xidmətlərin sayı (silmə qoruması üçün)
    long countByQrup_Id(Long qrupId);
}