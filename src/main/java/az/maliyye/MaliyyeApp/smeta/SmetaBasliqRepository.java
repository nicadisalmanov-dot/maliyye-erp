package az.maliyye.MaliyyeApp.smeta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmetaBasliqRepository extends JpaRepository<SmetaBasliq, Long> {
    List<SmetaBasliq> findBySmetaIdOrderByIdAsc(Long smetaId);
}