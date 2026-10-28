package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/kontragentler")
public class KontragentController {

    @Autowired private KontragentService service;
    @Autowired private AlisQaimesiRepository alisQaimesiRepo;
    @Autowired private SatisQaimesiRepository satisQaimesiRepo;

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

    // YENİ: Redaktə səhifəsini göstər
    @GetMapping("/redakte/{id}")
    public String redakteSehife(@PathVariable Long id, Model model) {
        model.addAttribute("kontragent", service.hamisiniGetir().stream()
                .filter(k -> k.getId().equals(id)).findFirst().orElseThrow());
        return "kontragent-redakte";
    }

    // YENİ: Redaktəni yadda saxla
    @PostMapping("/redakte/{id}")
    public String redakteEt(@PathVariable Long id,
                            @RequestParam String ad,
                            @RequestParam(required = false) String telefon,
                            @RequestParam(required = false) String email,
                            @RequestParam(required = false) String unvan,
                            @RequestParam String nov) {
        service.redakteEt(id, ad, telefon, email, unvan, nov);
        return "redirect:/kontragentler";
    }

    // YENİ: Silinmə qoruması ilə sil
    @PostMapping("/sil/{id}")
    public String sil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Yoxlama 1: Alış qaimələrində istifadə olunubmu?
            long alisSayi = alisQaimesiRepo.countByKontragent_Id(id);
            // Yoxlama 2: Satış qaimələrində istifadə olunubmu?
            long satisSayi = satisQaimesiRepo.countByKontragent_Id(id);

            if (alisSayi > 0 || satisSayi > 0) {
                String mesaj = "XƏTA: Bu kontragent ";
                if (alisSayi > 0) mesaj += alisSayi + " alış qaiməsində, ";
                if (satisSayi > 0) mesaj += satisSayi + " satış qaiməsində ";
                mesaj += "istifadə olunub! Silinə bilməz.";
                redirectAttributes.addFlashAttribute("xeta", mesaj);
                return "redirect:/kontragentler";
            }

            service.sil(id);
            redirectAttributes.addFlashAttribute("ugur", "Kontragent uğurla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/kontragentler";
    }
}