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

    // YENİ: Müəyyən bir qrupda olan məhsulların sayı (silmə qoruması üçün)
    public long qrupdaMehsulSayi(Long qrupId) {
        return mehsulRepository.countByQrup_Id(qrupId);
    }

    // --- Məhsullar ---
    public List<Mehsul> butunMehsullar() {
        return mehsulRepository.findAll();
    }

    public Mehsul mehsulElaveEt(String ad, Long qrupId, BigDecimal alisQiymeti, BigDecimal satisQiymeti, Double miqdar) {
        MehsulQrupu qrup = qrupRepository.findById(qrupId).orElseThrow();
        Mehsul yeni = new Mehsul(ad, qrup, alisQiymeti, satisQiymeti, miqdar);
        return mehsulRepository.save(yeni);
    }

    public void mehsulSil(Long id) {
        mehsulRepository.deleteById(id);
    }
}