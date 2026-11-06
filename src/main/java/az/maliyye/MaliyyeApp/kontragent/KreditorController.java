package az.maliyye.MaliyyeApp.kontragent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Controller
@RequestMapping("/kreditorlar")
public class KreditorController {

    @Autowired
    private KreditorService kreditorService;

    @GetMapping
    public String siyahidekor(Model model) {
        model.addAttribute("siyahilik", kreditorService.hamisiniGetir());
        model.addAttribute("kreditor", new Kreditor());
        return "kontragent/kreditorlar";
    }

    @PostMapping("/elave-et")
    public String elaveEt(@ModelAttribute Kreditor kreditor, Principal principal, RedirectAttributes redirectAttributes) {
        String istifadeci = principal != null ? principal.getName() : "Admin";
        kreditorService.yaddaSaxla(kreditor, istifadeci);
        redirectAttributes.addFlashAttribute("ugur", "Kreditor uğurla qeydiyyata alındı.");
        return "redirect:/kreditorlar";
    }

    @PostMapping("/sil/{id}")
    public String sil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        kreditorService.sil(id);
        redirectAttributes.addFlashAttribute("ugur", "Kreditor silindi.");
        return "redirect:/kreditorlar";
    }
}