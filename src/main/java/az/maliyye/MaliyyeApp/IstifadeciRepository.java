package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IstifadeciRepository extends JpaRepository<Istifadeci, Long> {
    // Spring Security üçün istifadəçini adına görə tapmaq
    Istifadeci findByUsername(String username);
    // Bu anbara təyin edilmiş istifadəçilərin sayı
    long countByAllowedWarehouses_Id(Long id);

    // Bu kassa hesabına təyin edilmiş istifadəçilərin sayı
    long countByAllowedKassaHesablari_Id(Long id);

    // Bu bank hesabına təyin edilmiş istifadəçilərin sayı
    long countByAllowedBankHesablari_Id(Long id);
}