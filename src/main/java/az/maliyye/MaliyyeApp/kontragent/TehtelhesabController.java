package az.maliyye.MaliyyeApp.kontragent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Controller
@RequestMapping("/tehtelhesab")
public class TehtelhesabController {

    @Autowired
    private TehtelhesabService tehtelhesabService;

    // 1. Səhifəni açan və siyahını yükləyən metod
    @GetMapping
    public String tehtelhesabSehife(Model model) {
        model.addAttribute("siyahilik", tehtelhesabService.hamisiniGetir());
        model.addAttribute("sexs", new TehtelhesabSexs()); // HTML-də th:object="${sexs}" ilə uyğundur
        return "kontragent/tehtelhesab";
    }

    // 2. Yeni şəxs əlavə edən metod
    @PostMapping("/elave-et")
    public String elaveEt(@ModelAttribute TehtelhesabSexs sexs, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String istifadeci = (principal != null) ? principal.getName() : "Admin";
            tehtelhesabService.elaveEt(sexs, istifadeci);
            redirectAttributes.addFlashAttribute("ugur", "Təhtəlhesab şəxs uğurla əlavə olundu.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/tehtelhesab";
    }

    // 3. Şəxsi silən metod
    @PostMapping("/sil/{id}")
    public String sil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tehtelhesabService.sil(id);
            redirectAttributes.addFlashAttribute("ugur", "Təhtəlhesab şəxs silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/tehtelhesab";
    }

    // 4. Redaktə səhifəsini açan metod
    @GetMapping("/redakte/{id}")
    public String redakteSehife(@PathVariable Long id, Model model) {
        model.addAttribute("sexs", tehtelhesabService.tapId(id));
        return "kontragent/tehtelhesab-redakte";
    }

    // 5. Redaktəni yadda saxlayan metod
    @PostMapping("/redakte/{id}")
    public String redakteEt(@PathVariable Long id, @ModelAttribute TehtelhesabSexs sexs, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String istifadeci = (principal != null) ? principal.getName() : "Admin";
            tehtelhesabService.redakteEt(id, sexs, istifadeci);
            redirectAttributes.addFlashAttribute("ugur", "Təhtəlhesab şəxs uğurla yeniləndi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/tehtelhesab";
    }
}