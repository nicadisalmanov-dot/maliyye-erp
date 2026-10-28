package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnbarService {

    @Autowired private MehsulQrupuRepository qrupRepository;
    @Autowired private MalTipiRepository malTipiRepo; // YENİ
    @Autowired private MehsulRepository mehsulRepository;
    @Autowired private AnbarRepository anbarRepository;
    @Autowired private AnbarMehsulRepository anbarMehsulRepository;
    @Autowired private IstifadeciRepository istifadeciRepo;
    @Autowired private SatisQaimesiSetriRepository satisSetriRepo;
    @Autowired private AlisQaimesiSetriRepository alisSetriRepo;

    public List<MehsulQrupu> butunQruplar() { return qrupRepository.findAll(); }
    public MehsulQrupu qrupElaveEt(String ad) { return qrupRepository.save(new MehsulQrupu(ad)); }
    public void qrupSil(Long id) { qrupRepository.deleteById(id); }
    public long qrupdaMehsulSayi(Long qrupId) { return mehsulRepository.countByQrup_Id(qrupId); }

    public List<Mehsul> butunMehsullar() { return mehsulRepository.findAll(); }

    @Transactional
    public Mehsul mehsulElaveEt(String ad, Long qrupId, Long malTipiId, String vahid, BigDecimal alisQiymeti, BigDecimal satisQiymeti, Long anbarId, Double miqdar) {
        if (anbarId == null) {
            throw new RuntimeException("Məhsul əlavə etmək üçün mütləq anbar seçilməlidir!");
        }

        if (mehsulRepository.existsByAd(ad.trim())) {
            throw new RuntimeException("Bu adda artıq başqa məhsul mövcuddur! Eyni adla ikinci məhsul yaratmaq olmaz.");
        }

        MehsulQrupu qrup = qrupRepository.findById(qrupId).orElseThrow();
        MalTipi malTipi = malTipiId != null ? malTipiRepo.findById(malTipiId).orElse(null) : null;

        Double dMiqdar = (miqdar != null) ? miqdar : 0.0;
        Mehsul yeni = new Mehsul(ad.trim(), qrup, malTipi, alisQiymeti, satisQiymeti, dMiqdar);
        yeni.setVahid(vahid);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Istifadeci cariIstifadeci = istifadeciRepo.findByUsername(auth.getName());
            yeni.setYaradanIstifadeci(cariIstifadeci);
        }

        Anbar anbar = anbarRepository.findById(anbarId).orElseThrow();
        yeni.setAnbar(anbar);
        yeni.setMiqdar(dMiqdar);

        Mehsul savedMehsul = mehsulRepository.save(yeni);

        AnbarMehsul anbarMehsul = new AnbarMehsul();
        anbarMehsul.setAnbar(anbar);
        anbarMehsul.setMehsul(savedMehsul);
        anbarMehsul.setMiqdar(dMiqdar);
        anbarMehsul.setOrtaDeyer(alisQiymeti);
        anbarMehsul.setQaliqDeyeri(alisQiymeti.multiply(BigDecimal.valueOf(dMiqdar)));
        anbarMehsulRepository.save(anbarMehsul);

        return savedMehsul;
    }

    @Transactional
    public void mehsulRedakteEt(Long id, String ad, Long qrupId, Long malTipiId, String vahid, BigDecimal alisQiymeti, BigDecimal satisQiymeti, Long anbarId, Double yeniMiqdar) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Istifadeci user = istifadeciRepo.findByUsername(auth.getName());
            if (user == null || !"ROLE_ADMIN".equals(user.getRol())) {
                throw new RuntimeException("Məhsulu redaktə etmək yalnız Admin səlahiyyətinə malik şəxslərə icazə verilir!");
            }
        }

        if (anbarId == null) {
            throw new RuntimeException("Məhsulu redaktə edərkən anbar seçilməsi məcburidir!");
        }

        Mehsul mehsul = mehsulRepository.findById(id).orElseThrow();

        Mehsul eyniAdliMehsul = mehsulRepository.findByAd(ad.trim());
        if (eyniAdliMehsul != null && !eyniAdliMehsul.getId().equals(id)) {
            throw new RuntimeException("Bu adda başqa məhsul mövcuddur!");
        }

        MehsulQrupu qrup = qrupRepository.findById(qrupId).orElseThrow();
        MalTipi malTipi = malTipiId != null ? malTipiRepo.findById(malTipiId).orElse(null) : null;

        mehsul.setAd(ad.trim());
        mehsul.setQrup(qrup);
        mehsul.setMalTipi(malTipi);
        mehsul.setVahid(vahid);
        mehsul.setAlisQiymeti(alisQiymeti);
        mehsul.setSatisQiymeti(satisQiymeti);
        Double dYeniMiqdar = (yeniMiqdar != null) ? yeniMiqdar : 0.0;
        mehsul.setMiqdar(dYeniMiqdar);
        mehsul.setDuzelisTarixi(LocalDateTime.now());

        Anbar anbar = anbarRepository.findById(anbarId).orElseThrow();
        mehsul.setAnbar(anbar);
        mehsulRepository.save(mehsul);

        List<AnbarMehsul> movcudQaliqlar = anbarMehsulRepository.findAll().stream()
                .filter(am -> am.getMehsul().getId().equals(id)).toList();

        if (!movcudQaliqlar.isEmpty()) {
            for (AnbarMehsul am : movcudQaliqlar) {
                if (dYeniMiqdar < 0) {
                    throw new RuntimeException("XƏTA: Məhsul miqdarı mənfi ola bilməz!");
                }
                am.setAnbar(anbar);
                am.setMiqdar(dYeniMiqdar);
                am.setOrtaDeyer(alisQiymeti);
                am.setQaliqDeyeri(alisQiymeti.multiply(BigDecimal.valueOf(dYeniMiqdar)));
                anbarMehsulRepository.save(am);
            }
        } else {
            AnbarMehsul yeniAm = new AnbarMehsul();
            yeniAm.setAnbar(anbar);
            yeniAm.setMehsul(mehsul);
            yeniAm.setMiqdar(dYeniMiqdar);
            yeniAm.setOrtaDeyer(alisQiymeti);
            yeniAm.setQaliqDeyeri(alisQiymeti.multiply(BigDecimal.valueOf(dYeniMiqdar)));
            anbarMehsulRepository.save(yeniAm);
        }
    }

    @Transactional
    public void mehsulSil(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Istifadeci user = istifadeciRepo.findByUsername(auth.getName());
            if (user == null || !"ROLE_ADMIN".equals(user.getRol())) {
                throw new RuntimeException("Məhsulu silmək yalnız Admin səlahiyyətinə malik şəxslərə icazə verilir!");
            }
        }

        boolean satisdaIstifadeOlunub = alisSetriRepo.existsByMehsul_Id(id) || satisSetriRepo.existsByMehsul_Id(id);
        if (satisdaIstifadeOlunub) {
            throw new RuntimeException("Bu məhsul qaimələrdə istifadə olunub, silmək olmaz!");
        }

        List<AnbarMehsul> anbarMehsullar = anbarMehsulRepository.findAll().stream()
                .filter(am -> am.getMehsul().getId().equals(id)).toList();
        anbarMehsulRepository.deleteAll(anbarMehsullar);
        mehsulRepository.deleteById(id);
    }

    public List<AnbarMehsul> anbarQaliqlari(Long anbarId) {
        if (anbarId != null) return anbarMehsulRepository.findByAnbar_Id(anbarId);
        else return anbarMehsulRepository.findAll();
    }

    public List<AnbarMehsul> qruplasdirilmisQaliqlar() {
        List<AnbarMehsul> butun = anbarMehsulRepository.findAll();
        Map<Long, AnbarMehsul> netice = new LinkedHashMap<>();
        Map<Long, List<Anbar>> anbarMap = new LinkedHashMap<>();

        for (AnbarMehsul am : butun) {
            Long mehsulId = am.getMehsul().getId();
            anbarMap.computeIfAbsent(mehsulId, k -> new ArrayList<>()).add(am.getAnbar());

            if (netice.containsKey(mehsulId)) {
                AnbarMehsul movcud = netice.get(mehsulId);
                Double yeniMiqdar = movcud.getMiqdar() + am.getMiqdar();
                movcud.setMiqdar(yeniMiqdar);
                BigDecimal yeniQaliqDeyeri = movcud.getQaliqDeyeri().add(am.getQaliqDeyeri());
                movcud.setQaliqDeyeri(yeniQaliqDeyeri);
                if (yeniMiqdar > 0) {
                    movcud.setOrtaDeyer(yeniQaliqDeyeri.divide(BigDecimal.valueOf(yeniMiqdar), 2, java.math.RoundingMode.HALF_UP));
                } else { movcud.setOrtaDeyer(BigDecimal.ZERO); }
            } else {
                AnbarMehsul kopya = new AnbarMehsul();
                kopya.setMehsul(am.getMehsul()); kopya.setAnbar(am.getAnbar());
                kopya.setMiqdar(am.getMiqdar()); kopya.setOrtaDeyer(am.getOrtaDeyer());
                kopya.setQaliqDeyeri(am.getQaliqDeyeri());
                netice.put(mehsulId, kopya);
            }
        }

        for (AnbarMehsul am : netice.values()) {
            am.setButunAnbarlar(anbarMap.get(am.getMehsul().getId()));
        }

        return new ArrayList<>(netice.values());
    }

    public List<Anbar> icazeliAnbarlar(String username) {
        Istifadeci user = istifadeciRepo.findByUsername(username);
        if (user != null && user.getRol().equals("ROLE_ADMIN")) return anbarRepository.findAll();
        return user != null ? user.getAllowedWarehouses() : new ArrayList<>();
    }
}