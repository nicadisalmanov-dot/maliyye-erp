package az.maliyye.MaliyyeApp.Anbar_emeliyyatlari;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XidmetQrupuRepository extends JpaRepository<XidmetQrupu, Long> {
}