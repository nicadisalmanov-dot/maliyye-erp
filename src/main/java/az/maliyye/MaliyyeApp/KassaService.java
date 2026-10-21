package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class KassaService {

    @Autowired
    private KassaEmeliyyatiRepository repository;

    // Kassa səhifəsindəki axtarış və filterlər üçün
    public List<KassaEmeliyyati> filterle(String axtaris, String ay, String nov) {
        List<KassaEmeliyyati> netice = repository.findAll();

        if (axtaris != null && !axtaris.trim().isEmpty()) {
            String kicikHerf = axtaris.trim().toLowerCase();
            netice = netice.stream()
                    .filter(e -> e.getTesvir().toLowerCase().contains(kicikHerf) ||
                            e.getSenedNomresi().toLowerCase().contains(kicikHerf))
                    .toList();
        }

        if (ay != null && !ay.isEmpty()) {
            LocalDate baslangic = LocalDate.parse(ay + "-01");
            LocalDate son = baslangic.withDayOfMonth(baslangic.lengthOfMonth());
            netice = netice.stream()
                    .filter(e -> !e.getTarix().isBefore(baslangic) && !e.getTarix().isAfter(son))
                    .toList();
        }

        if (nov != null && !nov.isEmpty()) {
            netice = netice.stream()
                    .filter(e -> e.getNov().equals(nov))
                    .toList();
        }

        return netice;
    }

    public KassaEmeliyyati elaveEt(KassaEmeliyyati emeliyyat) {
        return repository.save(emeliyyat);
    }

    public void sil(Long id) {
        repository.deleteById(id);
    }

    // Bütün məlumatlar üçün (Ana Səhifə və digər hesablamalar)
    public List<KassaEmeliyyati> hamisiniGetir() {
        return repository.findAll();
    }

    // Statistika metodları (Siyahı qəbul edir)
    public BigDecimal medaxilCemi(List<KassaEmeliyyati> siyahi) {
        BigDecimal cem = BigDecimal.ZERO;
        for (KassaEmeliyyati e : siyahi) {
            if (e.isMedaxil()) cem = cem.add(e.getMebleg());
        }
        return cem;
    }

    public BigDecimal mexaricCemi(List<KassaEmeliyyati> siyahi) {
        BigDecimal cem = BigDecimal.ZERO;
        for (KassaEmeliyyati e : siyahi) {
            if (e.isMexaric()) cem = cem.add(e.getMebleg());
        }
        return cem;
    }

    public BigDecimal kassaQaligi(List<KassaEmeliyyati> siyahi) {
        return medaxilCemi(siyahi).subtract(mexaricCemi(siyahi));
    }
}