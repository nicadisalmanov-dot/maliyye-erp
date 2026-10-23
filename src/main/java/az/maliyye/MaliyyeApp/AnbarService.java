package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnbarService {

    @Autowired private MehsulQrupuRepository qrupRepository;
    @Autowired private MehsulRepository mehsulRepository;
    @Autowired private IstifadeciRepository istifadeciRepo;

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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Istifadeci cariIstifadeci = istifadeciRepo.findByUsername(auth.getName());
            yeni.setYaradanIstifadeci(cariIstifadeci);
        }

        return mehsulRepository.save(yeni);
    }

    // YENİ: Məhsulu redaktə et
    public Mehsul mehsulRedakteEt(Long id, String ad, Long qrupId, BigDecimal alisQiymeti, BigDecimal satisQiymeti, Double miqdar) {
        Mehsul m = mehsulRepository.findById(id).orElseThrow();
        m.setAd(ad);
        m.setQrup(qrupRepository.findById(qrupId).orElseThrow());
        m.setAlisQiymeti(alisQiymeti);
        m.setSatisQiymeti(satisQiymeti);
        m.setMiqdar(miqdar);
        m.setDuzelisTarixi(LocalDateTime.now());
        return mehsulRepository.save(m);
    }

    public void mehsulSil(Long id) {
        mehsulRepository.deleteById(id);
    }
}