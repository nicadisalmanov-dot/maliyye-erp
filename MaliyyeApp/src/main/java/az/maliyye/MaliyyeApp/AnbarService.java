package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AnbarService {

    @Autowired
    private MehsulQrupuRepository qrupRepository;

    @Autowired
    private MehsulRepository mehsulRepository;

    // --- Qruplar ---
    public List<MehsulQrupu> butunQruplar() {
        return qrupRepository.findAll();
    }

    public MehsulQrupu qrupElaveEt(String ad) {
        return qrupRepository.save(new MehsulQrupu(ad));
    }

    public void qrupSil(Long id) {
        qrupRepository.deleteById(id);
    }

    // --- Məhsullar ---
    public List<Mehsul> butunMehsullar() {
        return mehsulRepository.findAll();
    }

    public Mehsul mehsulElaveEt(String ad, Long qrupId, BigDecimal qiymet, Double miqdar) {
        MehsulQrupu qrup = qrupRepository.findById(qrupId).orElseThrow();
        Mehsul yeni = new Mehsul(ad, qrup, qiymet, miqdar);
        return mehsulRepository.save(yeni);
    }

    public void mehsulSil(Long id) {
        mehsulRepository.deleteById(id);
    }
}