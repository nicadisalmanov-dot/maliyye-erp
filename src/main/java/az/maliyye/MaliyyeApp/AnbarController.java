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

    // 1. Əsas Menyu (Şəkildəki kimi)
    @GetMapping
    public String anbarMenyu() {
        return "anbar"; // Yuxarıda yaratdığımız menyu səhifəsi
    }

    // 2. Mallar və Qruplar (Köhnə funksionallıq)
    @GetMapping("/mallar")
    public String mallarSehife(Model model) {
        model.addAttribute("qruplar", service.butunQruplar());
        model.addAttribute("mehsullar", service.butunMehsullar());
        return "mallar"; // Yeni yaratdığımız fayl
    }

    // Digər bölmələr üçün müvəqqəti boş səhifələr
    @GetMapping("/{sehife}")
    public String bosSehife(@PathVariable String sehife, Model model) {
        model.addAttribute("sehifeAdi", sehife);
        return "tezlikle"; // Bütün boş səhifələr üçün bir şablon
    }

    // --- Köhnə POST əməliyyatları (Qruplar və Məhsullar üçün) ---
    @PostMapping("/qrup-elave-et")
    public String qrupElaveEt(@RequestParam String qrupAdi) {
        if(qrupAdi != null && !qrupAdi.isEmpty()) {
            service.qrupElaveEt(qrupAdi);
        }
        return "redirect:/anbar/mallar";
    }

    @PostMapping("/qrup-sil/{id}")
    public String qrupSil(@PathVariable Long id) {
        service.qrupSil(id);
        return "redirect:/anbar/mallar";
    }

    @PostMapping("/mehsul-elave-et")
    public String mehsulElaveEt(@RequestParam String mehsulAdi,
                                @RequestParam Long qrupId,
                                @RequestParam String alisQiymeti,
                                @RequestParam String satisQiymeti,
                                @RequestParam String miqdar) {
        BigDecimal alisQiymetiDec = new BigDecimal(alisQiymeti);
        BigDecimal satisQiymetiDec = new BigDecimal(satisQiymeti);
        Double miqdarDouble = Double.parseDouble(miqdar);

        service.mehsulElaveEt(mehsulAdi, qrupId, alisQiymetiDec, satisQiymetiDec, miqdarDouble);
        return "redirect:/anbar/mallar";

    }

    @PostMapping("/mehsul-sil/{id}")
    public String mehsulSil(@PathVariable Long id) {
        service.mehsulSil(id);
        return "redirect:/anbar/mallar";
    }
}