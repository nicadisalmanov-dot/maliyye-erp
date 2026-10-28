package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HesabatController {

    @Autowired private KontragentService kontragentService;
    @Autowired private SatisQaimesiRepository satisRepo;
    @Autowired private AlisQaimesiRepository alisRepo;
    @Autowired private KassaEmeliyyatiRepository kassaRepo;
    @Autowired private BankEmeliyyatiRepository bankRepo;

    public static class HesabatSetri {
        public String kontragentAd;
        public BigDecimal evveleQaliq = BigDecimal.ZERO;
        public BigDecimal satisMebleg = BigDecimal.ZERO;
        public BigDecimal alisMebleg = BigDecimal.ZERO;
        public BigDecimal odenis = BigDecimal.ZERO;
        public BigDecimal qaliq = BigDecimal.ZERO;
        public boolean musteri;

        public HesabatSetri(String ad, boolean musteri) {
            this.kontragentAd = ad;
            this.musteri = musteri;
        }
    }

    @GetMapping("/hesabat")
    public String hesabatSehife(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baslangicParam,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sonParam,
            Model model) {

        final LocalDate baslangic = (baslangicParam != null) ? baslangicParam : LocalDate.now().withDayOfYear(1);
        final LocalDate son = (sonParam != null) ? sonParam : LocalDate.now();

        model.addAttribute("baslangic", baslangic);
        model.addAttribute("son", son);

        List<HesabatSetri> debitorlar = new ArrayList<>();
        List<HesabatSetri> kreditorlar = new ArrayList<>();

        // --- DEBİTORLAR (MÜŞTƏRİLƏR) ---
        List<Kontragent> musteriler = kontragentService.musteriGetir();
        for (Kontragent k : musteriler) {
            HesabatSetri hs = new HesabatSetri(k.getAd(), true);

            // 1. Satış qaimələrindən gələn məbləğ
            BigDecimal satisMebleg = satisRepo.findBySilinibFalseAndKontragent_Id(k.getId()).stream()
                    .filter(q -> !q.getTarix().isBefore(baslangic) && !q.getTarix().isAfter(son))
                    .map(q -> q.getUmumiMebleg() != null ? q.getUmumiMebleg() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            hs.satisMebleg = satisMebleg;

            // 2. Kassa və Bank mədaxillərindən ödənişlər
            // YENİ: isMedaxilVar() və getMedaxil() istifadə edirik
            BigDecimal kassaOdenis = kassaRepo.findAll().stream()
                    .filter(e -> e.getKontragent() != null && e.getKontragent().getId().equals(k.getId())
                            && e.isMedaxilVar()
                            && !e.getTarix().isBefore(baslangic) && !e.getTarix().isAfter(son))
                    .map(KassaEmeliyyati::getMedaxil)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal bankOdenis = bankRepo.findAll().stream()
                    .filter(e -> e.getKontragent() != null && e.getKontragent().getId().equals(k.getId())
                            && e.isMedaxil()
                            && !e.getTarix().isBefore(baslangic) && !e.getTarix().isAfter(son))
                    .map(BankEmeliyyati::getMebleg)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            hs.odenis = kassaOdenis.add(bankOdenis);
            hs.qaliq = hs.satisMebleg.subtract(hs.odenis);

            if (hs.satisMebleg.compareTo(BigDecimal.ZERO) > 0 || hs.odenis.compareTo(BigDecimal.ZERO) > 0) {
                debitorlar.add(hs);
            }
        }

        // --- KREDİTORLAR (TƏCHİZATÇILAR) ---
        List<Kontragent> techizatcilar = kontragentService.techizatciGetir();
        for (Kontragent k : techizatcilar) {
            HesabatSetri hs = new HesabatSetri(k.getAd(), false);

            // 1. Alış qaimələrindən gələn məbləğ
            BigDecimal alisMebleg = alisRepo.findBySilinibFalseAndKontragent_Id(k.getId()).stream()
                    .filter(q -> !q.getTarix().isBefore(baslangic) && !q.getTarix().isAfter(son))
                    .map(q -> q.getUmumiMebleg() != null ? q.getUmumiMebleg() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            hs.alisMebleg = alisMebleg;

            // 2. Kassa və Bank məxariclərindən ödənişlər
            // YENİ: isMexaricVar() və getMexaric() istifadə edirik
            BigDecimal kassaOdenis = kassaRepo.findAll().stream()
                    .filter(e -> e.getKontragent() != null && e.getKontragent().getId().equals(k.getId())
                            && e.isMexaricVar()
                            && !e.getTarix().isBefore(baslangic) && !e.getTarix().isAfter(son))
                    .map(KassaEmeliyyati::getMexaric)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal bankOdenis = bankRepo.findAll().stream()
                    .filter(e -> e.getKontragent() != null && e.getKontragent().getId().equals(k.getId())
                            && e.isMexaric()
                            && !e.getTarix().isBefore(baslangic) && !e.getTarix().isAfter(son))
                    .map(BankEmeliyyati::getMebleg)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            hs.odenis = kassaOdenis.add(bankOdenis);
            hs.qaliq = hs.alisMebleg.subtract(hs.odenis);

            if (hs.alisMebleg.compareTo(BigDecimal.ZERO) > 0 || hs.odenis.compareTo(BigDecimal.ZERO) > 0) {
                kreditorlar.add(hs);
            }
        }

        model.addAttribute("debitorlar", debitorlar);
        model.addAttribute("kreditorlar", kreditorlar);

        return "hesabat";
    }
}