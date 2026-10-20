package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/satinalma")
public class SatinalmaController {

    @Autowired
    private SatinalmaService satinalmaService;

    @Autowired
    private KontragentService kontragentService;

    @Autowired
    private AnbarService anbarService;

    @GetMapping
    public String satinalmaSehife(Model model) {
        model.addAttribute("satinalmalar", satinalmaService.butunSatinalmalar());
        model.addAttribute("techizatcilar", kontragentService.hamisiniGetir());
        model.addAttribute("mehsullar", anbarService.butunMehsullar());
        return "satinalma";
    }

    @PostMapping("/et")
    public String satinalmaEt(@RequestParam Long kontragentId,
                              @RequestParam Long mehsulId,
                              @RequestParam Double miqdar,
                              @RequestParam String qiymet) {
        BigDecimal qiymetDecimal = new BigDecimal(qiymet);
        satinalmaService.satinalmaEt(kontragentId, mehsulId, miqdar, qiymetDecimal);
        return "redirect:/satinalma";
    }

    @PostMapping("/sil/{id}")
    public String sil(@PathVariable Long id) {
        satinalmaService.sil(id);
        return "redirect:/satinalma";
    }
}