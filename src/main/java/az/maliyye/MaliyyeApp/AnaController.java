package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class AnaController {

    @Autowired private KassaService kassaService;
    @Autowired private BankService bankService;
    @Autowired private KontragentService kontragentService;
    @Autowired private IstifadeciRepository istifadeciRepo;

    @GetMapping("/")
    public String anaSehife(Model model) {
        // İstifadəçi məlumatlarını götürürük
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Istifadeci istifadeci = istifadeciRepo.findByUsername(username);

        // Əgər istifadəçinin Dashboard icazəsi yoxdursa və admin deyilsə, Anbara yönləndir
        if (istifadeci != null && !istifadeci.getRol().equals("ROLE_ADMIN") && !istifadeci.isIcazeDashboard()) {
            return "redirect:/anbar";
        }

        List<KassaEmeliyyati> butunKassa = kassaService.filterle(null, null, null);
        List<BankEmeliyyati> butunBank = bankService.hamisiniGetir();

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

        // 12 aylıq hesablama
        int cariIl = java.time.LocalDate.now().getYear();
        List<Double> ayligGelir = new java.util.ArrayList<>();
        List<Double> ayligXerc = new java.util.ArrayList<>();

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

        return "index";
    }
}