package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/anbar")
public class AnbarController {

    @Autowired
    private AnbarService service;

    @GetMapping
    public String anbarMenyu() {
        return "anbar";
    }

    @GetMapping("/mallar")
    public String mallarSehife(Model model) {
        model.addAttribute("qruplar", service.butunQruplar());
        model.addAttribute("mehsullar", service.butunMehsullar());
        return "mallar";
    }

    @GetMapping("/esas-vesaitler")
    public String esasVesaitlerSehife() {
        return "esas-vesaitler";
    }

    @GetMapping("/qeyri-maddi")
    public String qeyriMaddiSehife() {
        return "qeyri-maddi";
    }

    @GetMapping("/{sehife}")
    public String bosSehife(@PathVariable String sehife, Model model) {
        model.addAttribute("sehifeAdi", sehife);
        return "tezlikle";
    }

    @PostMapping("/qrup-elave-et")
    public String qrupElaveEt(@RequestParam String qrupAdi) {
        if(qrupAdi != null && !qrupAdi.isEmpty()) {
            service.qrupElaveEt(qrupAdi);
        }
        return "redirect:/anbar/mallar";
    }

    @PostMapping("/qrup-sil/{id}")
    public String qrupSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            long mehsulSayi = service.qrupdaMehsulSayi(id);
            if (mehsulSayi > 0) {
                redirectAttributes.addFlashAttribute("xeta", "XƏTA: Bu qrupda " + mehsulSayi + " məhsul var! Əvvəlcə məhsulları silin və ya başqa qrupa keçirin.");
                return "redirect:/anbar/mallar";
            }
            service.qrupSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Məhsul qrupu uğurla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
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
    public String mehsulSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // GƏLƏCƏKDƏ: Satış və ya Alışda istifadə olunubsa silinməsin
            service.mehsulSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Məhsul uğurla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Məhsul silinə bilmədi. Səbəb: " + e.getMessage());
        }
        return "redirect:/anbar/mallar";
    }
}