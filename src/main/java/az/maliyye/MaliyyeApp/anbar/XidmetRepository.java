package az.maliyye.MaliyyeApp.anbar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XidmetRepository extends JpaRepository<Xidmet, Long> {
    long countByQrup_Id(Long qrupId);
}