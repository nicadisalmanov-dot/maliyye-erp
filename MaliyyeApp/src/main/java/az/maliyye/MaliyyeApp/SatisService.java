package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SatisService {

    @Autowired
    private SatisRepository satisRepository;

    @Autowired
    private MehsulRepository mehsulRepository;

    @Autowired
    private KontragentRepository kontragentRepository;

    public List<Satis> butunSatislar() {
        return satisRepository.findAll();
    }

    public void satisEt(Long kontragentId, Long mehsulId, Double miqdar, BigDecimal qiymet) {
        // 1. Müştəri və Məhsulu tap
        Kontragent kontragent = kontragentRepository.findById(kontragentId)
                .orElseThrow(() -> new RuntimeException("Kontragent tapılmadı"));
        Mehsul mehsul = mehsulRepository.findById(mehsulId)
                .orElseThrow(() -> new RuntimeException("Məhsul tapılmadı"));

        // 2. Yoxla: Anbarda kifayət qədər mal var?
        if (mehsul.getMiqdar() < miqdar) {
            throw new RuntimeException("Anbarda kifayət qədər mal yoxdur! Mövcud: " + mehsul.getMiqdar());
        }

        // 3. Məhsulun miqdarını azalt
        mehsul.setMiqdar(mehsul.getMiqdar() - miqdar);
        mehsulRepository.save(mehsul);

        // 4. Ümumi məbləği hesabla (Qiymət * Miqdar)
        BigDecimal umumiMebleg = qiymet.multiply(BigDecimal.valueOf(miqdar));

        // 5. Satışı qeyd et
        Satis yeniSatis = new Satis(LocalDate.now(), kontragent, mehsul, miqdar, qiymet, umumiMebleg);
        satisRepository.save(yeniSatis);
    }

    public void sil(Long id) {
        satisRepository.deleteById(id);
    }
}