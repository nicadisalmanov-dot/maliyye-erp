package az.maliyye.MaliyyeApp.smeta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SmetaRepository extends JpaRepository<Smeta, Long> {
    List<Smeta> findByLayiheId(Long layiheId);
}