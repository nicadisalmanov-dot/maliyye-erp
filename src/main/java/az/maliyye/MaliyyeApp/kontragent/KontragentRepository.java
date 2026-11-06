package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KontragentRepository extends JpaRepository<Kontragent, Long> {

    // YENİ: Müştəri və ya Hər ikisi olanları gətirir
    List<Kontragent> findByNovIn(List<String> novler);
}