package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EmeliyyatService {

    @Autowired
    private EmeliyyatRepository repository;

    // Bütün əməliyyatları gətir
    public List<Emeliyyat> hamisiniGetir() {
        return repository.findAll();
    }

    // Yeni əməliyyat əlavə et
    public Emeliyyat elaveEt(Emeliyyat emeliyyat) {
        return repository.save(emeliyyat);
    }

    // Əməliyyatı sil
    public void sil(Long id) {
        repository.deleteById(id);
    }

    // Ümumi balansı hesabla
    public BigDecimal balans() {
        BigDecimal cem = BigDecimal.ZERO;
        for (Emeliyyat e : repository.findAll()) {
            cem = cem.add(e.getMebleg());
        }
        return cem;
    }

    // Yalnız gəlirlərin cəmi
    public BigDecimal gelirler() {
        BigDecimal cem = BigDecimal.ZERO;
        for (Emeliyyat e : repository.findAll()) {
            if (e.getMebleg().signum() > 0) {
                cem = cem.add(e.getMebleg());
            }
        }
        return cem;
    }

    // Yalnız xərclərin cəmi
    public BigDecimal xercler() {
        BigDecimal cem = BigDecimal.ZERO;
        for (Emeliyyat e : repository.findAll()) {
            if (e.getMebleg().signum() < 0) {
                cem = cem.add(e.getMebleg());
            }
        }
        return cem;
    }
}