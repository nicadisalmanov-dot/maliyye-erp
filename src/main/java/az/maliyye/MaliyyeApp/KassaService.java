package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class KassaService {

    @Autowired private KassaEmeliyyatiRepository repository;
    @Autowired private IstifadeciRepository istifadeciRepo;
    @Autowired private KassaHesabiRepository kassaHesabiRepo;

    public List<KassaEmeliyyati> hamisiniGetir() {
        try { return repository.findAll(); } catch (Exception e) { return new ArrayList<>(); }
    }

    public List<KassaEmeliyyati> filterle(String axtaris, String ay, String nov, Boolean tesdiq) {
        List<KassaEmeliyyati> netice = hamisiniGetir();
        if (axtaris != null && !axtaris.trim().isEmpty()) {
            String k = axtaris.trim().toLowerCase();
            netice = netice.stream()
                    .filter(e -> (e.getTesvir() != null && e.getTesvir().toLowerCase().contains(k)) ||
                            (e.getSenedNomresi() != null && e.getSenedNomresi().toLowerCase().contains(k)))
                    .toList();
        }
        if (ay != null && !ay.isEmpty()) {
            LocalDate baslangic = LocalDate.parse(ay + "-01");
            LocalDate son = baslangic.withDayOfMonth(baslangic.lengthOfMonth());
            netice = netice.stream()
                    .filter(e -> e.getTarix() != null && !e.getTarix().isBefore(baslangic) && !e.getTarix().isAfter(son))
                    .toList();
        }
        if (nov != null && !nov.isEmpty()) {
            if ("MEDAXIL".equals(nov)) {
                netice = netice.stream().filter(e -> e.getMedaxil() != null && e.getMedaxil().compareTo(BigDecimal.ZERO) > 0).toList();
            } else if ("MEXARIC".equals(nov)) {
                netice = netice.stream().filter(e -> e.getMexaric() != null && e.getMexaric().compareTo(BigDecimal.ZERO) > 0).toList();
            }
        }
        if (tesdiq != null) {
            netice = netice.stream().filter(e -> tesdiq.equals(e.getTesdiqlenib())).toList();
        }
        return netice;
    }

    @Transactional
    public KassaEmeliyyati elaveEt(KassaEmeliyyati emeliyyat) {
        if (emeliyyat.getSenedNomresi() == null || emeliyyat.getSenedNomresi().isEmpty()) {
            long say = repository.count() + 1;
            emeliyyat.setSenedNomresi("K-" + String.format("%05d", say));
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Istifadeci cariIstifadeci = istifadeciRepo.findByUsername(auth.getName());
            emeliyyat.setYaradanIstifadeci(cariIstifadeci);
        }
        if (emeliyyat.getMedaxil() == null) emeliyyat.setMedaxil(BigDecimal.ZERO);
        if (emeliyyat.getMexaric() == null) emeliyyat.setMexaric(BigDecimal.ZERO);
        return repository.save(emeliyyat);
    }

    @Transactional
    public void redakteEt(Long id, KassaEmeliyyati yeni) {
        KassaEmeliyyati k = repository.findById(id).orElseThrow();
        k.setTarix(yeni.getTarix());
        k.setLayihe(yeni.getLayihe());
        k.setKassaHesabi(yeni.getKassaHesabi());
        k.setHesablasmaHesabi(yeni.getHesablasmaHesabi());
        k.setKontragent(yeni.getKontragent());
        k.setTesvir(yeni.getTesvir());
        k.setMedaxil(yeni.getMedaxil() != null ? yeni.getMedaxil() : BigDecimal.ZERO);
        k.setMexaric(yeni.getMexaric() != null ? yeni.getMexaric() : BigDecimal.ZERO);
        k.setXercMaddesi(yeni.getXercMaddesi());
        k.setDuzelisTarixi(LocalDateTime.now());
        repository.save(k);
    }

    @Transactional
    public void sil(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void tesdiqle(Long id) {
        KassaEmeliyyati k = repository.findById(id).orElseThrow();
        k.setTesdiqlenib(true);
        k.setTesdiqTarixi(LocalDateTime.now());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Istifadeci c = istifadeciRepo.findByUsername(auth.getName());
            k.setTesdiqleyenIstifadeci(c);
        }
        repository.save(k);
    }

    public KassaEmeliyyati findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public BigDecimal medaxilCemi(List<KassaEmeliyyati> siyahi) {
        if (siyahi == null) return BigDecimal.ZERO;
        return siyahi.stream()
                .map(e -> e.getMedaxil() != null ? e.getMedaxil() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal mexaricCemi(List<KassaEmeliyyati> siyahi) {
        if (siyahi == null) return BigDecimal.ZERO;
        return siyahi.stream()
                .map(e -> e.getMexaric() != null ? e.getMexaric() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal kassaQaligi(List<KassaEmeliyyati> siyahi) {
        return medaxilCemi(siyahi).subtract(mexaricCemi(siyahi));
    }

    // YENİ: Hər kassa hesabı üzrə qalıq
    public List<KassaHesabiQaliq> kassaHesablariQaliqlari(String username) {
        Istifadeci user = istifadeciRepo.findByUsername(username);
        boolean isAdmin = user != null && user.getRol().equals("ROLE_ADMIN");

        List<KassaHesabi> hesablar;
        if (isAdmin) {
            hesablar = kassaHesabiRepo.findAll();
        } else if (user != null && user.getAllowedKassaHesablari() != null) {
            hesablar = user.getAllowedKassaHesablari();
        } else {
            hesablar = new ArrayList<>();
        }

        List<KassaHesabiQaliq> netice = new ArrayList<>();
        for (KassaHesabi h : hesablar) {
            BigDecimal medaxil = repository.findAll().stream()
                    .filter(e -> e.getKassaHesabi() != null && e.getKassaHesabi().getId().equals(h.getId()))
                    .map(e -> e.getMedaxil() != null ? e.getMedaxil() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal mexaric = repository.findAll().stream()
                    .filter(e -> e.getKassaHesabi() != null && e.getKassaHesabi().getId().equals(h.getId()))
                    .map(e -> e.getMexaric() != null ? e.getMexaric() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            KassaHesabiQaliq q = new KassaHesabiQaliq();
            q.setAd(h.getAd());
            q.setMedaxil(medaxil);
            q.setMexaric(mexaric);
            q.setQaliq(medaxil.subtract(mexaric));
            netice.add(q);
        }
        return netice;
    }

    // Köməkçi sinif
    public static class KassaHesabiQaliq {
        private String ad;
        private BigDecimal medaxil;
        private BigDecimal mexaric;
        private BigDecimal qaliq;

        public String getAd() { return ad; }
        public void setAd(String ad) { this.ad = ad; }
        public BigDecimal getMedaxil() { return medaxil; }
        public void setMedaxil(BigDecimal medaxil) { this.medaxil = medaxil; }
        public BigDecimal getMexaric() { return mexaric; }
        public void setMexaric(BigDecimal mexaric) { this.mexaric = mexaric; }
        public BigDecimal getQaliq() { return qaliq; }
        public void setQaliq(BigDecimal qaliq) { this.qaliq = qaliq; }
    }
}