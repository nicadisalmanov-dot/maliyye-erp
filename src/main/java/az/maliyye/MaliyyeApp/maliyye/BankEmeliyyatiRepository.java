package az.maliyye.MaliyyeApp.maliyye;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankEmeliyyatiRepository extends JpaRepository<BankEmeliyyati, Long> {
}