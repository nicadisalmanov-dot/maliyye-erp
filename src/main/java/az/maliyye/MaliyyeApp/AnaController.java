package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.ArrayList;

@Controller
public class AnaController {

    @Autowired private KassaService kassaService;
    @Autowired private BankService bankService;
    @Autowired private KontragentService kontragentService;
    @Autowired private IstifadeciRepository istifadeciRepo;

    @GetMapping("/")
    public String anaSehife(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Istifadeci istifadeci = istifadeciRepo.findByUsername(username);

        boolean isAdmin = istifadeci != null && istifadeci.getRol().equals("ROLE_ADMIN");
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("icazeDashboard", isAdmin || (istifadeci != null && istifadeci.isIcazeDashboard()));
        model.addAttribute("icazeKassa", isAdmin || (istifadeci != null && istifadeci.isIcazeKassa()));
        model.addAttribute("icazeBank", isAdmin || (istifadeci != null && istifadeci.isIcazeBank()));
        model.addAttribute("icazeKontragent", isAdmin || (istifadeci != null && istifadeci.isIcazeKontragent()));
        model.addAttribute("icazeAnbar", isAdmin || (istifadeci != null && istifadeci.isIcazeAnbar()));
        model.addAttribute("icazeSatis", isAdmin || (istifadeci != null && istifadeci.isIcazeSatis()));
        model.addAttribute("icazeSatinalma", isAdmin || (istifadeci != null && istifadeci.isIcazeSatinalma()));
        model.addAttribute("icazeHesabat", isAdmin || (istifadeci != null && istifadeci.isIcazeHesabat()));

        if (istifadeci != null && !isAdmin && !istifadeci.isIcazeDashboard()) {
            if (istifadeci.isIcazeAnbar()) return "redirect:/anbar";
            if (istifadeci.isIcazeKassa()) return "redirect:/kassa";
            if (istifadeci.isIcazeBank()) return "redirect:/bank";
            if (istifadeci.isIcazeSatis()) return "redirect:/satis";
            if (istifadeci.isIcazeSatinalma()) return "redirect:/satinalma";
            if (istifadeci.isIcazeHesabat()) return "redirect:/hesabat";
            return "redirect:/login?accessDenied";
        }

        // BÜTÜN MƏLUMATLARI BOŞ GÖNDƏRİRİK Kİ, XƏTA OLMASIN
        model.addAttribute("kassaQaliq", BigDecimal.ZERO);
        model.addAttribute("bankQaliq", BigDecimal.ZERO);
        model.addAttribute("umumiMedaxil", BigDecimal.ZERO);
        model.addAttribute("umumiMexaric", BigDecimal.ZERO);
        model.addAttribute("xalisMenfeet", BigDecimal.ZERO);
        model.addAttribute("sonKassa", new ArrayList<>());
        model.addAttribute("kontragentSayi", 0);
        model.addAttribute("ayligGelir", new ArrayList<Double>());
        model.addAttribute("ayligXerc", new ArrayList<Double>());

        return "index";
    }
}