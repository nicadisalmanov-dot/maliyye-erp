package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/anbar/xidmetler")
public class XidmetController {

    @Autowired
    private XidmetService service;

    @GetMapping
    public String xidmetSehife(Model model) {
        model.addAttribute("qruplar", service.butunQruplar());
        model.addAttribute("xidmetler", service.butunXidmetler());
        return "xidmetler";
    }

    // --- QRUP REDAKTƏ ---
    @GetMapping("/qrup-redakte/{id}")
    public String qrupRedakteSehife(@PathVariable Long id, Model model) {
        model.addAttribute("qrup", service.butunQruplar().stream()
                .filter(q -> q.getId().equals(id)).findFirst().orElseThrow());
        return "xidmet-qrup-redakte";
    }

    @PostMapping("/qrup-redakte/{id}")
    public String qrupRedakteEt(@PathVariable Long id, @RequestParam String ad) {
        service.qrupRedakteEt(id, ad);
        return "redirect:/anbar/xidmetler";
    }

    @PostMapping("/qrup-elave-et")
    public String qrupElaveEt(@RequestParam String qrupAdi) {
        if(qrupAdi != null && !qrupAdi.isEmpty()) service.qrupElaveEt(qrupAdi);
        return "redirect:/anbar/xidmetler";
    }

    @PostMapping("/qrup-sil/{id}")
    public String qrupSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            long sayi = service.qrupdaXidmetSayi(id);
            if (sayi > 0) {
                redirectAttributes.addFlashAttribute("xeta", "XƏTA: Bu qrupda " + sayi + " xidmət var! Əvvəlcə onları silin.");
                return "redirect:/anbar/xidmetler";
            }
            service.qrupSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Xidmət qrupu silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/xidmetler";
    }

    // --- XİDMƏT REDAKTƏ ---
    @GetMapping("/xidmet-redakte/{id}")
    public String xidmetRedakteSehife(@PathVariable Long id, Model model) {
        model.addAttribute("xidmet", service.butunXidmetler().stream()
                .filter(x -> x.getId().equals(id)).findFirst().orElseThrow());
        model.addAttribute("qruplar", service.butunQruplar());
        return "xidmet-redakte";
    }

    @PostMapping("/xidmet-redakte/{id}")
    public String xidmetRedakteEt(@PathVariable Long id,
                                  @RequestParam String ad,
                                  @RequestParam Long qrupId,
                                  @RequestParam String nov,
                                  @RequestParam String vahid,
                                  @RequestParam(required = false) String qiymet) {
        // YENİ: Əgər qiymət boşdursa, 0 kimi qəbul edirik
        BigDecimal qiymetDec = (qiymet != null && !qiymet.trim().isEmpty()) ? new BigDecimal(qiymet) : BigDecimal.ZERO;

        service.xidmetRedakteEt(id, ad, qrupId, nov, vahid, qiymetDec);
        return "redirect:/anbar/xidmetler";
    }

    @PostMapping("/xidmet-elave-et")
    public String xidmetElaveEt(@RequestParam String ad,
                                @RequestParam Long qrupId,
                                @RequestParam String nov,
                                @RequestParam String vahid,
                                @RequestParam(required = false) String qiymet) {
        // YENİ: Əgər qiymət boşdursa, 0 kimi qəbul edirik
        BigDecimal qiymetDec = (qiymet != null && !qiymet.trim().isEmpty()) ? new BigDecimal(qiymet) : BigDecimal.ZERO;

        service.xidmetElaveEt(ad, qrupId, nov, vahid, qiymetDec);
        return "redirect:/anbar/xidmetler";
    }

    @PostMapping("/xidmet-sil/{id}")
    public String xidmetSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.xidmetSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Xidmət silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Xidmət silinə bilmədi. Səbəb: " + e.getMessage());
        }
        return "redirect:/anbar/xidmetler";
    }
}