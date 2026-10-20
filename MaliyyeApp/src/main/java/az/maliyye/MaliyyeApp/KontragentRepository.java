package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KontragentRepository extends JpaRepository<Kontragent, Long> {
    // Növə görə filterləmək üçün (Müştərilər və ya Təchizatçılar)
    List<Kontragent> findByNov(String nov);
}