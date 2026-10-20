package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AnaController {

    @Autowired
    private KassaService kassaService;

    @Autowired
    private BankService bankService;

    @Autowired
    private KontragentService kontragentService;

    @GetMapping("/")
    public String anaSehife(Model model) {
        List<KassaEmeliyyati> butunKassa = kassaService.hamisiniGetir();
        List<BankEmeliyyati> butunBank = bankService.hamisiniGetir();

        // --- Ümumi hesablamalar (Bütün zamanlar üçün) ---
        BigDecimal kassaQaliq = kassaService.kassaQaligi(butunKassa);
        BigDecimal bankQaliq = bankService.bankQaligi();

        BigDecimal umumiMedaxil = kassaService.medaxilCemi(butunKassa).add(bankService.medaxilCemi());
        BigDecimal umumiMexaric = kassaService.mexaricCemi(butunKassa).add(bankService.mexaricCemi());
        BigDecimal xalisMenfeet = umumiMedaxil.subtract(umumiMexaric);

        model.addAttribute("kassaQaliq", kassaQaliq);
        model.addAttribute("bankQaliq", bankQaliq);
        model.addAttribute("umumiMedaxil", umumiMedaxil);
        model.addAttribute("umumiMexaric", umumiMexaric);
        model.addAttribute("xalisMenfeet", xalisMenfeet);

        model.addAttribute("sonKassa", butunKassa);
        model.addAttribute("kontragentSayi", kontragentService.hamisiniGetir().size());

        // --- 12 AYLIK HESABLAMA (Cari il üçün) ---
        int cariIl = LocalDate.now().getYear();
        List<Double> ayligGelir = new ArrayList<>();
        List<Double> ayligXerc = new ArrayList<>();

        for (int ay = 1; ay <= 12; ay++) {
            BigDecimal ayGelir = BigDecimal.ZERO;
            BigDecimal ayXerc = BigDecimal.ZERO;

            for (KassaEmeliyyati e : butunKassa) {
                if (e.getTarix().getYear() == cariIl && e.getTarix().getMonthValue() == ay) {
                    if (e.isMedaxil()) ayGelir = ayGelir.add(e.getMebleg());
                    if (e.isMexaric()) ayXerc = ayXerc.add(e.getMebleg());
                }
            }
            for (BankEmeliyyati e : butunBank) {
                if (e.getTarix().getYear() == cariIl && e.getTarix().getMonthValue() == ay) {
                    if (e.isMedaxil()) ayGelir = ayGelir.add(e.getMebleg());
                    if (e.isMexaric()) ayXerc = ayXerc.add(e.getMebleg());
                }
            }
            ayligGelir.add(ayGelir.doubleValue());
            ayligXerc.add(ayXerc.doubleValue());
        }

        model.addAttribute("ayligGelir", ayligGelir);
        model.addAttribute("ayligXerc", ayligXerc);

        // --- FAİZ HESABLAMASI və CARİ AYIN MƏLUMATLARI ---
        LocalDate indi = LocalDate.now();
        int cariAy = indi.getMonthValue();
        int cariIl2 = indi.getYear();
        int kecenAy = cariAy == 1 ? 12 : cariAy - 1;
        int kecenAyIl = cariAy == 1 ? cariIl2 - 1 : cariIl2;

        BigDecimal cariAyGelir = BigDecimal.ZERO;
        BigDecimal kecenAyGelir = BigDecimal.ZERO;
        BigDecimal cariAyXerc = BigDecimal.ZERO;
        BigDecimal kecenAyXerc = BigDecimal.ZERO;

        for (KassaEmeliyyati e : butunKassa) {
            if (e.getTarix().getYear() == cariIl2 && e.getTarix().getMonthValue() == cariAy) {
                if (e.isMedaxil()) cariAyGelir = cariAyGelir.add(e.getMebleg());
                if (e.isMexaric()) cariAyXerc = cariAyXerc.add(e.getMebleg());
            }
            if (e.getTarix().getYear() == kecenAyIl && e.getTarix().getMonthValue() == kecenAy) {
                if (e.isMedaxil()) kecenAyGelir = kecenAyGelir.add(e.getMebleg());
                if (e.isMexaric()) kecenAyXerc = kecenAyXerc.add(e.getMebleg());
            }
        }
        for (BankEmeliyyati e : butunBank) {
            if (e.getTarix().getYear() == cariIl2 && e.getTarix().getMonthValue() == cariAy) {
                if (e.isMedaxil()) cariAyGelir = cariAyGelir.add(e.getMebleg());
                if (e.isMexaric()) cariAyXerc = cariAyXerc.add(e.getMebleg());
            }
            if (e.getTarix().getYear() == kecenAyIl && e.getTarix().getMonthValue() == kecenAy) {
                if (e.isMedaxil()) kecenAyGelir = kecenAyGelir.add(e.getMebleg());
                if (e.isMexaric()) kecenAyXerc = kecenAyXerc.add(e.getMebleg());
            }
        }

        // Cari ayın mənfəəti
        BigDecimal cariAyMenfeet = cariAyGelir.subtract(cariAyXerc);

        // Cari ayın məlumatlarını HTML-ə göndəririk
        model.addAttribute("cariAyGelir", cariAyGelir);
        model.addAttribute("cariAyXerc", cariAyXerc);
        model.addAttribute("cariAyMenfeet", cariAyMenfeet);

        // Faizləri hesablayırıq
        model.addAttribute("gelirFaiz", hesablaFaiz(cariAyGelir, kecenAyGelir));
        model.addAttribute("xercFaiz", hesablaFaiz(cariAyXerc, kecenAyXerc));

        return "index";
    }

    private double hesablaFaiz(BigDecimal cari, BigDecimal kecen) {
        if (kecen.compareTo(BigDecimal.ZERO) == 0) {
            return cari.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        BigDecimal ferq = cari.subtract(kecen);
        BigDecimal faiz = ferq.divide(kecen, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        return faiz.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}