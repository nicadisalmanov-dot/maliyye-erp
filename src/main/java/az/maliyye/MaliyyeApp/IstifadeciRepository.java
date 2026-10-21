package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IstifadeciRepository extends JpaRepository<Istifadeci, Long> {
    // Spring Security üçün istifadəçini adına görə tapmaq
    Istifadeci findByUsername(String username);
}