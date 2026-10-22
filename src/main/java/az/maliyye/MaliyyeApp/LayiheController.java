package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/layiheler")
public class LayiheController {

    @Autowired
    private LayiheRepository layiheRepo;

    @Autowired
    private AnbarRepository anbarRepo;

    @Autowired
    private KassaHesabiRepository kassaHesabiRepo;

    @Autowired
    private BankHesabiRepository bankHesabiRepo;

    @GetMapping
    public String layiheSehife(Model model) {
        model.addAttribute("layiheler", layiheRepo.findAll());
        model.addAttribute("anbarlar", anbarRepo.findAll());
        model.addAttribute("kassaHesablari", kassaHesabiRepo.findAll());
        model.addAttribute("bankHesablari", bankHesabiRepo.findAll());
        return "layiheler";
    }

    // --- LAYİHƏ ƏMƏLİYYATLARI ---
    @PostMapping("/layihe-elave-et")
    public String layiheElaveEt(@RequestParam String ad) {
        if (ad != null && !ad.trim().isEmpty()) {
            layiheRepo.save(new Layihe(ad.trim()));
        }
        return "redirect:/layiheler";
    }

    @PostMapping("/layihe-sil/{id}")
    public String layiheSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Layihe layihe = layiheRepo.findById(id).orElseThrow();

        // 1. Bağlı anbarları yoxla
        List<Anbar> bagliAnbarlar = anbarRepo.findAll().stream()
                .filter(a -> a.getLayiheler() != null && a.getLayiheler().contains(layihe))
                .toList();

        if (!bagliAnbarlar.isEmpty()) {
            String anbarAdlari = bagliAnbarlar.stream().map(Anbar::getAd).reduce((a, b) -> a + ", " + b).orElse("");
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Bu Layihəyə bağlı Anbarlar var! (" + anbarAdlari + "). Əvvəlcə onları silin və ya başqa layihəyə keçirin.");
            return "redirect:/layiheler";
        }

        // 2. Əgər heç bir bağlı anbar yoxdursa, layihəni sil
        layiheRepo.delete(layihe);
        redirectAttributes.addFlashAttribute("ugur", "Layihə uğurla silindi.");
        return "redirect:/layiheler";
    }

    // --- ANBAR ƏMƏLİYYATLARI ---
    @PostMapping("/anbar-elave-et")
    public String anbarElaveEt(@RequestParam String ad, @RequestParam(required = false) List<Long> layiheler) {
        Anbar anbar = new Anbar(ad.trim());
        if (layiheler != null) {
            anbar.setLayiheler(layiheRepo.findAllById(layiheler));
        }
        anbarRepo.save(anbar);
        return "redirect:/layiheler";
    }

    @PostMapping("/anbar-sil/{id}")
    public String anbarSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Anbar anbar = anbarRepo.findById(id).orElseThrow();

        // GƏLƏCƏKDƏ: Əgər bu anbarda məhsul varsa, silinməsin
        boolean anbardaMehsulVar = false;

        if (anbardaMehsulVar) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Bu Anbarda olan məhsullar var! Əvvəlcə onları başqa anbara köçürün.");
        } else {
            anbarRepo.delete(anbar);
            redirectAttributes.addFlashAttribute("ugur", "Anbar uğurla silindi.");
        }
        return "redirect:/layiheler";
    }

    // --- KASSA HESABLARI ---
    @PostMapping("/kassa-hesabi-elave-et")
    public String kassaHesabiElaveEt(@RequestParam String ad) {
        if (ad != null && !ad.trim().isEmpty()) {
            kassaHesabiRepo.save(new KassaHesabi(ad.trim()));
        }
        return "redirect:/layiheler";
    }

    @PostMapping("/kassa-hesabi-sil/{id}")
    public String kassaHesabiSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // GƏLƏCƏKDƏ: Əgər bu kassada əməliyyat varsa, silinməsin
        kassaHesabiRepo.deleteById(id);
        redirectAttributes.addFlashAttribute("ugur", "Kassa hesabı silindi.");
        return "redirect:/layiheler";
    }

    // --- BANK HESABLARI ---
    @PostMapping("/bank-hesabi-elave-et")
    public String bankHesabiElaveEt(@RequestParam String ad) {
        if (ad != null && !ad.trim().isEmpty()) {
            bankHesabiRepo.save(new BankHesabi(ad.trim()));
        }
        return "redirect:/layiheler";
    }

    @PostMapping("/bank-hesabi-sil/{id}")
    public String bankHesabiSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // GƏLƏCƏKDƏ: Əgər bu bank hesabında əməliyyat varsa, silinməsin
        bankHesabiRepo.deleteById(id);
        redirectAttributes.addFlashAttribute("ugur", "Bank hesabı silindi.");
        return "redirect:/layiheler";
    }
}