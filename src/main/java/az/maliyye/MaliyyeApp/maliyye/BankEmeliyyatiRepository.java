package az.maliyye.MaliyyeApp.bank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankEmeliyyatiRepository extends JpaRepository<BankEmeliyyati, Long> {
}