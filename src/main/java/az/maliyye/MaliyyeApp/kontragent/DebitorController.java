package az.maliyye.MaliyyeApp.kontragent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Controller
@RequestMapping("/debitorlar")
public class DebitorController {

    @Autowired
    private DebitorService debitorService;

    @GetMapping
    public String siyahidekor(Model model) {
        model.addAttribute("siyahilik", debitorService.hamisiniGetir());
        model.addAttribute("debitor", new Debitor());
        return "kontragent/debitorlar";
    }

    @PostMapping("/elave-et")
    public String elaveEt(@ModelAttribute Debitor debitor, Principal principal, RedirectAttributes redirectAttributes) {
        String istifadeci = principal != null ? principal.getName() : "Admin";
        debitorService.yaddaSaxla(debitor, istifadeci);
        redirectAttributes.addFlashAttribute("ugur", "Debitor uğurla qeydiyyata alındı.");
        return "redirect:/debitorlar";
    }

    @PostMapping("/sil/{id}")
    public String sil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        debitorService.sil(id);
        redirectAttributes.addFlashAttribute("ugur", "Debitor silindi.");
        return "redirect:/debitorlar";
    }
}