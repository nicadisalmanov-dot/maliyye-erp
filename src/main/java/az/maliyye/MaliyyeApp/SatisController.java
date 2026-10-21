package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/anbar/satis")
public class SatisController {

    @Autowired
    private SatisService satisService;

    @Autowired
    private KontragentService kontragentService;

    @Autowired
    private AnbarService anbarService;

    @GetMapping
    public String satisSehife(Model model) {
        model.addAttribute("satislar", satisService.butunSatislar());
        model.addAttribute("musteriler", kontragentService.hamisiniGetir());
        model.addAttribute("mehsullar", anbarService.butunMehsullar());
        return "satis";
    }

    @PostMapping("/et")
    public String satisEt(@RequestParam Long kontragentId,
                          @RequestParam Long mehsulId,
                          @RequestParam Double miqdar,
                          @RequestParam String qiymet,
                          Model model) {
        try {
            BigDecimal qiymetDecimal = new BigDecimal(qiymet);
            satisService.satisEt(kontragentId, mehsulId, miqdar, qiymetDecimal);
            return "redirect:/satis";
        } catch (RuntimeException e) {
            // Əgər anbarda mal yoxdursa, xəta mesajı ilə geri qaytar
            model.addAttribute("xeta", e.getMessage());
            model.addAttribute("satislar", satisService.butunSatislar());
            model.addAttribute("musteriler", kontragentService.hamisiniGetir());
            model.addAttribute("mehsullar", anbarService.butunMehsullar());
            return "satis";
        }
    }

    @PostMapping("/sil/{id}")
    public String sil(@PathVariable Long id) {
        satisService.sil(id);
        return "redirect:/satis";
    }
}