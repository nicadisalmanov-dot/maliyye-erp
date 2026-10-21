package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/kontragentler")
public class KontragentController {

    @Autowired
    private KontragentService service;

    @GetMapping
    public String kontragentSehife(Model model) {
        model.addAttribute("kontragentler", service.hamisiniGetir());
        return "kontragentler";
    }

    @PostMapping("/elave-et")
    public String elaveEt(@RequestParam String ad,
                          @RequestParam(required = false) String telefon,
                          @RequestParam(required = false) String email,
                          @RequestParam(required = false) String unvan,
                          @RequestParam String nov) {
        Kontragent yeni = new Kontragent(ad, telefon, email, unvan, nov);
        service.elaveEt(yeni);
        return "redirect:/kontragentler";
    }

    @PostMapping("/sil/{id}")
    public String sil(@PathVariable Long id) {
        service.sil(id);
        return "redirect:/kontragentler";
    }
}