package az.maliyye.MaliyyeApp.bank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankHesabiRepository extends JpaRepository<BankHesabi, Long> {
}