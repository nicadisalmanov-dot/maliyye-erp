package az.maliyye.MaliyyeApp.kontragent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Controller
@RequestMapping("/kontragentler/borc-muqavileleri")
public class BorcController {

    @Autowired
    private BorcService borcService;

    @GetMapping
    public String borcSehife(Model model) {
        model.addAttribute("muqavileler", borcService.hamisiniGetir());
        return "kontragent/borc-muqavileleri";
    }

    @PostMapping("/elave-et")
    public String elaveEt(@ModelAttribute BorcMuqavilesi muqavile, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String istifadeci = (principal != null) ? principal.getName() : "Admin";
            borcService.elaveEt(muqavile, istifadeci);
            redirectAttributes.addFlashAttribute("ugur", "Borc müqaviləsi uğurla əlavə olundu.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/kontragentler/borc-muqavileleri";
    }

    @GetMapping("/redakte/{id}")
    public String redakteSehife(@PathVariable Long id, Model model) {
        model.addAttribute("muqavile", borcService.tapId(id));
        return "kontragent/borc-redakte";
    }

    @PostMapping("/redakte/{id}")
    public String redakteEt(@PathVariable Long id, @ModelAttribute BorcMuqavilesi muqavile, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String istifadeci = (principal != null) ? principal.getName() : "Admin";
            borcService.redakteEt(id, muqavile, istifadeci);
            redirectAttributes.addFlashAttribute("ugur", "Borc müqaviləsi uğurla yeniləndi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/kontragentler/borc-muqavileleri";
    }

    @PostMapping("/sil/{id}")
    public String sil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            borcService.sil(id);
            redirectAttributes.addFlashAttribute("ugur", "Borc müqaviləsi silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/kontragentler/borc-muqavileleri";
    }
}