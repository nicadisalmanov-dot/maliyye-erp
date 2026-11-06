package az.maliyye.MaliyyeApp.smeta;

import az.maliyye.MaliyyeApp.Layihe;
import az.maliyye.MaliyyeApp.LayiheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SmetaService {

    @Autowired
    private SmetaRepository smetaRepository;

    @Autowired
    private SmetaBendiRepository smetaBendiRepository;

    @Autowired
    private BudceRepository budceRepository;

    @Autowired
    private LayiheRepository layiheRepository;

    public List<Smeta> butunSmetalar() {
        return smetaRepository.findAll();
    }

    public List<Smeta> layiheyeGoreSmetalar(Long layiheId) {
        return smetaRepository.findByLayiheId(layiheId);
    }

    public Smeta smetaTap(Long id) {
        return smetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Smeta tapılmadı: ID " + id));
    }

    @Transactional
    public void smetaYarat(Long layiheId, String ad, String versiya, String valyuta, String istifadeci) {
        Layihe layihe = layiheRepository.findById(layiheId)
                .orElseThrow(() -> new RuntimeException("Layihə tapılmadı!"));

        Smeta smeta = new Smeta();
        smeta.setLayihe(layihe);
        smeta.setAd(ad);
        smeta.setVersiya(versiya != null && !versiya.isEmpty() ? versiya : "v1.0");
        smeta.setValyuta(valyuta != null && !valyuta.isEmpty() ? valyuta : "AZN");
        smeta.setStatus("QARALAMA");
        smeta.setYaradanIstifadeci(istifadeci);
        smeta.setYaradilmaTarixi(LocalDateTime.now());

        smetaRepository.save(smeta);
    }

    @Transactional
    public void bəndElaveEt(Long smetaId, String isAdi, String vahid, Double miqdar, Double vahidQiymeti, String kateqoriya) {
        Smeta smeta = smetaTap(smetaId);

        SmetaBendi bendi = new SmetaBendi();
        bendi.setSmeta(smeta);
        bendi.setIsAdi(isAdi);
        bendi.setVahid(vahid);
        bendi.setMiqdar(miqdar != null ? miqdar : 0.0);
        bendi.setVahidQiymeti(vahidQiymeti != null ? vahidQiymeti : 0.0);
        bendi.setKateqoriya(kateqoriya != null ? kateqoriya : "MATERIAL");

        smetaBendiRepository.save(bendi);
    }

    @Transactional
    public void bəndSil(Long bendiId) {
        smetaBendiRepository.deleteById(bendiId);
    }

    @Transactional
    public void smetaSil(Long id) {
        smetaRepository.deleteById(id);
    }

    @Transactional
    public void budceyeCevirVeTesdiqle(Long smetaId, Double gozlenilmezFaiz, Double dolayiFaiz, String istifadeci) {
        Smeta smeta = smetaTap(smetaId);

        Budce budce = smeta.getBudce();
        if (budce == null) {
            budce = new Budce();
            budce.setSmeta(smeta);
        }

        budce.setGozlenilmezFaiz(gozlenilmezFaiz != null ? gozlenilmezFaiz : 0.0);
        budce.setDolayiXerclerFaizi(dolayiFaiz != null ? dolayiFaiz : 0.0);
        budce.hesablaVeYenile();
        budce.setTesdiqleyenIstifadeci(istifadeci);
        budce.setTesdiqTarixi(LocalDateTime.now());

        budceRepository.save(budce);

        smeta.setStatus("TESDIQLENMIS");
        smetaRepository.save(smeta);
    }
}