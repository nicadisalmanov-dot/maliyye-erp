package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SatinalmaService {

    @Autowired
    private SatinalmaRepository satinalmaRepository;

    @Autowired
    private MehsulRepository mehsulRepository;

    @Autowired
    private KontragentRepository kontragentRepository;

    public List<Satinalma> butunSatinalmalar() {
        return satinalmaRepository.findAll();
    }

    public void satinalmaEt(Long kontragentId, Long mehsulId, Double miqdar, BigDecimal qiymet) {
        Kontragent techizatci = kontragentRepository.findById(kontragentId)
                .orElseThrow(() -> new RuntimeException("Təchizatçı tapılmadı"));
        Mehsul mehsul = mehsulRepository.findById(mehsulId)
                .orElseThrow(() -> new RuntimeException("Məhsul tapılmadı"));

        // Ən əsas hissə: Mal alındığı üçün miqdar ARTIRILIR
        mehsul.setMiqdar(mehsul.getMiqdar() + miqdar);
        mehsulRepository.save(mehsul);

        // Ümumi məbləğ
        BigDecimal umumiMebleg = qiymet.multiply(BigDecimal.valueOf(miqdar));

        Satinalma yeni = new Satinalma(LocalDate.now(), techizatci, mehsul, miqdar, qiymet, umumiMebleg);
        satinalmaRepository.save(yeni);
    }

    public void sil(Long id) {
        // Qeyd: Əgər satınalmanı silsək, anbardaki miqdarı geri azaltmaq lazımdır.
        // Amma sadəlik üçün hələlik sadəcə qeydi silirik. (Sonra təkmilləşdirərik)
        satinalmaRepository.deleteById(id);
    }
}