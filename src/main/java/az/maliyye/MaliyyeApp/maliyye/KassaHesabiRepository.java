package az.maliyye.MaliyyeApp.kassa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KassaHesabiRepository extends JpaRepository<KassaHesabi, Long> {
}