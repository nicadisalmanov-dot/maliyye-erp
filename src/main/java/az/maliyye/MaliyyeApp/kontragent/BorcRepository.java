package az.maliyye.MaliyyeApp.kontragent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorcRepository extends JpaRepository<BorcMuqavilesi, Long> {
}