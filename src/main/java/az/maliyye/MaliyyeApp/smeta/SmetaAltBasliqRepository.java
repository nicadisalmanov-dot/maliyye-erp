package az.maliyye.MaliyyeApp.smeta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmetaAltBasliqRepository extends JpaRepository<SmetaAltBasliq, Long> {
}