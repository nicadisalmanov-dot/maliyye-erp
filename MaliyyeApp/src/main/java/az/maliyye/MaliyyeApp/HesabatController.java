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

    @Autowired
    private KontragentService kontragentService;
    @Autowired
    private SatisService satisService;
    @Autowired
    private SatinalmaService satinalmaService;
    @Autowired
    private KassaService kassaService;
    @Autowired
    private BankService bankService;

    public static class HesabatSetri {
        public String kontragentAd;
        public BigDecimal evveleQaliq = BigDecimal.ZERO;
        public BigDecimal satisMebleg = BigDecimal.ZERO; // Dövrdəki borclanma (Müştəri üçün)
        public BigDecimal alisMebleg = BigDecimal.ZERO;  // Dövrdəki borclanma (Təchizatçı üçün)
        public BigDecimal odenis = BigDecimal.ZERO;      // Dövrdəki ödəniş
        public BigDecimal qaliq = BigDecimal.ZERO;       // Yekun qalıq
        public boolean musteri;

        public HesabatSetri(String ad, boolean musteri) {
            this.kontragentAd = ad;
            this.musteri = musteri;
        }
    }

    @GetMapping("/hesabat")
    public String hesabatSehife(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baslangic,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate son,
            Model model) {

        // Əgər tarix seçilməyibsə, default olaraq cari ilin əvvəlini və bugünü götürürük
        if (baslangic == null) baslangic = LocalDate.now().withDayOfYear(1);
        if (son == null) son = LocalDate.now();

        List<Kontragent> butunKontragentler = kontragentService.hamisiniGetir();

        List<HesabatSetri> debitorlar = new ArrayList<>();
        List<HesabatSetri> kreditorlar = new ArrayList<>();

        List<Satis> satislar = satisService.butunSatislar();
        List<Satinalma> satinalmalar = satinalmaService.butunSatinalmalar();
        List<KassaEmeliyyati> kassalar = kassaService.hamisiniGetir();
        List<BankEmeliyyati> banklar = bankService.hamisiniGetir();

        for (Kontragent k : butunKontragentler) {
            HesabatSetri setir = new HesabatSetri(k.getAd(), "MUSTERI".equals(k.getNov()) || "HER_IKISI".equals(k.getNov()));

            // --- 1. SATIŞLAR (Müştəri üçün) ---
            for (Satis s : satislar) {
                if (s.getKontragent() != null && s.getKontragent().getId().equals(k.getId())) {
                    if (s.getTarix().isBefore(baslangic)) {
                        setir.evveleQaliq = setir.evveleQaliq.add(s.getUmumiMebleg()); // Başlanğıcdan əvvəlki satışlar
                    } else if (!s.getTarix().isAfter(son)) { // (baslangic <= tarix <= son)
                        setir.satisMebleg = setir.satisMebleg.add(s.getUmumiMebleg()); // Dövrdəki satışlar
                    }
                }
            }

            // --- 2. SATINALMALAR (Təchizatçı üçün) ---
            for (Satinalma s : satinalmalar) {
                if (s.getKontragent() != null && s.getKontragent().getId().equals(k.getId())) {
                    if (s.getTarix().isBefore(baslangic)) {
                        setir.evveleQaliq = setir.evveleQaliq.add(s.getUmumiMebleg());
                    } else if (!s.getTarix().isAfter(son)) {
                        setir.alisMebleg = setir.alisMebleg.add(s.getUmumiMebleg());
                    }
                }
            }

            // --- 3. KASSA ÖDƏNİŞLƏRİ ---
            for (KassaEmeliyyati e : kassalar) {
                if (e.getKontragent() != null && e.getKontragent().getId().equals(k.getId())) {
                    if (e.getTarix().isBefore(baslangic)) {
                        if (e.isMedaxil()) setir.evveleQaliq = setir.evveleQaliq.subtract(e.getMebleg());
                        if (e.isMexaric()) setir.evveleQaliq = setir.evveleQaliq.subtract(e.getMebleg());
                    } else if (!e.getTarix().isAfter(son)) {
                        setir.odenis = setir.odenis.add(e.getMebleg());
                    }
                }
            }

            // --- 4. BANK ÖDƏNİŞLƏRİ ---
            for (BankEmeliyyati e : banklar) {
                if (e.getKontragent() != null && e.getKontragent().getId().equals(k.getId())) {
                    if (e.getTarix().isBefore(baslangic)) {
                        if (e.isMedaxil()) setir.evveleQaliq = setir.evveleQaliq.subtract(e.getMebleg());
                        if (e.isMexaric()) setir.evveleQaliq = setir.evveleQaliq.subtract(e.getMebleg());
                    } else if (!e.getTarix().isAfter(son)) {
                        setir.odenis = setir.odenis.add(e.getMebleg());
                    }
                }
            }

            // --- QALIQ HESABLAMASI ---
            if (setir.musteri) {
                // Müştəri üçün: (Əvvələ qalıq) + (Dövrdəki satış) - (Dövrdəki ödəniş)
                setir.qaliq = setir.evveleQaliq.add(setir.satisMebleg).subtract(setir.odenis);
                debitorlar.add(setir);
            } else {
                // Təchizatçı üçün: (Əvvələ qalıq) + (Dövrdəki alış) - (Dövrdəki ödəniş)
                setir.qaliq = setir.evveleQaliq.add(setir.alisMebleg).subtract(setir.odenis);
                kreditorlar.add(setir);
            }
        }

        model.addAttribute("debitorlar", debitorlar);
        model.addAttribute("kreditorlar", kreditorlar);
        model.addAttribute("baslangic", baslangic);
        model.addAttribute("son", son);

        return "hesabat";
    }
}