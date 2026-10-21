package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/anbar")
public class AnbarController {

    @Autowired
    private AnbarService service;

    @GetMapping
    public String anbarSehife(Model model) {
        model.addAttribute("qruplar", service.butunQruplar());
        model.addAttribute("mehsullar", service.butunMehsullar());
        return "anbar";
    }

    // Qrup əlavə et
    @PostMapping("/qrup-elave-et")
    public String qrupElaveEt(@RequestParam String qrupAdi) {
        if(qrupAdi != null && !qrupAdi.isEmpty()) {
            service.qrupElaveEt(qrupAdi);
        }
        return "redirect:/anbar";
    }

    // Qrup sil
    @PostMapping("/qrup-sil/{id}")
    public String qrupSil(@PathVariable Long id) {
        service.qrupSil(id);
        return "redirect:/anbar";
    }

    // Məhsul əlavə et
    @PostMapping("/mehsul-elave-et")
    public String mehsulElaveEt(@RequestParam String mehsulAdi,
                                @RequestParam Long qrupId,
                                @RequestParam String qiymet,
                                @RequestParam String miqdar) {
        BigDecimal qiymetDecimal = new BigDecimal(qiymet);
        Double miqdarDouble = Double.parseDouble(miqdar);
        service.mehsulElaveEt(mehsulAdi, qrupId, qiymetDecimal, miqdarDouble);
        return "redirect:/anbar";
    }

    // Məhsul sil
    @PostMapping("/mehsul-sil/{id}")
    public String mehsulSil(@PathVariable Long id) {
        service.mehsulSil(id);
        return "redirect:/anbar";
    }
}