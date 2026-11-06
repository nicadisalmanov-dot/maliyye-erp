package az.maliyye.MaliyyeApp.smeta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BudceRepository extends JpaRepository<Budce, Long> {
    Optional<Budce> findBySmetaId(Long smetaId);
}