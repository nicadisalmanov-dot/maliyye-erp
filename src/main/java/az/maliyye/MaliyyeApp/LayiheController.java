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

    @Autowired private LayiheRepository layiheRepo;
    @Autowired private AnbarRepository anbarRepo;
    @Autowired private KassaHesabiRepository kassaHesabiRepo;
    @Autowired private BankHesabiRepository bankHesabiRepo;
    @Autowired private IstifadeciRepository istifadeciRepo; // İstifadəçi yoxlaması üçün

    @GetMapping
    public String layiheSehife(Model model) {
        model.addAttribute("layiheler", layiheRepo.findAll());
        model.addAttribute("anbarlar", anbarRepo.findAll());
        model.addAttribute("kassaHesablari", kassaHesabiRepo.findAll());
        model.addAttribute("bankHesablari", bankHesabiRepo.findAll());
        return "layiheler";
    }

    // ==========================================
    // LAYİHƏ ƏMƏLİYYATLARI
    // ==========================================
    @PostMapping("/layihe-elave-et")
    public String layiheElaveEt(@RequestParam String ad) {
        if (ad != null && !ad.trim().isEmpty()) layiheRepo.save(new Layihe(ad.trim()));
        return "redirect:/layiheler";
    }

    @PostMapping("/layihe-sil/{id}")
    public String layiheSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // YOXLAMA 1: İstifadəçiyə təyin edilibmi?
            long istifadeciSayi = layiheRepo.istifadeciLayiheEloqesiSayi(id);
            if (istifadeciSayi > 0) {
                redirectAttributes.addFlashAttribute("xeta", "XƏTA: Bu layihə " + istifadeciSayi + " istifadəçiyə təyin edilib! Əvvəlcə istifadəçilərin icazələrini dəyişin.");
                return "redirect:/layiheler";
            }
            // YOXLAMA 2: Anbara bağlıdırmı?
            long anbarSayi = layiheRepo.anbarLayiheEloqesiSayi(id);
            if (anbarSayi > 0) {
                redirectAttributes.addFlashAttribute("xeta", "XƏTA: Bu layihəyə " + anbarSayi + " anbar bağlıdır! Əvvəlcə anbarları silin və ya başqa layihəyə keçirin.");
                return "redirect:/layiheler";
            }

            // GƏLƏCƏKDƏ BURADA YOXLAMA OLACAQ:
            // long satisSayi = satisRepo.countByLayihe_Id(id);
            // if (satisSayi > 0) { ... "Bu layihədə satış var" ... }

            layiheRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("ugur", "Layihə uğurla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/layiheler";
    }

    @GetMapping("/layihe-redakte/{id}")
    public String layiheRedakteSehife(@PathVariable Long id, Model model) {
        model.addAttribute("layihe", layiheRepo.findById(id).orElseThrow());
        return "layihe-redakte";
    }

    @PostMapping("/layihe-redakte/{id}")
    public String layiheRedakteEt(@PathVariable Long id, @RequestParam String ad) {
        Layihe l = layiheRepo.findById(id).orElseThrow();
        l.setAd(ad.trim());
        layiheRepo.save(l);
        return "redirect:/layiheler";
    }

    // ==========================================
    // ANBAR ƏMƏLİYYATLARI
    // ==========================================
    @PostMapping("/anbar-elave-et")
    public String anbarElaveEt(@RequestParam String ad, @RequestParam(required = false) List<Long> layiheler) {
        Anbar anbar = new Anbar(ad.trim());
        if (layiheler != null) anbar.setLayiheler(layiheRepo.findAllById(layiheler));
        anbarRepo.save(anbar);
        return "redirect:/layiheler";
    }

    @GetMapping("/anbar-redakte/{id}")
    public String anbarRedakteSehife(@PathVariable Long id, Model model) {
        model.addAttribute("anbar", anbarRepo.findById(id).orElseThrow());
        model.addAttribute("layiheler", layiheRepo.findAll());
        return "anbar-redakte";
    }

    @PostMapping("/anbar-redakte/{id}")
    public String anbarRedakteEt(@PathVariable Long id, @RequestParam String ad, @RequestParam(required = false) List<Long> layiheler) {
        Anbar a = anbarRepo.findById(id).orElseThrow();
        a.setAd(ad.trim());
        a.setLayiheler(layiheler != null ? layiheRepo.findAllById(layiheler) : null);
        anbarRepo.save(a);
        return "redirect:/layiheler";
    }

    @PostMapping("/anbar-sil/{id}")
    public String anbarSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // YOXLAMA 1: İstifadəçiyə təyin edilibmi?
            long istifadeciSayi = istifadeciRepo.countByAllowedWarehouses_Id(id);
            if (istifadeciSayi > 0) {
                redirectAttributes.addFlashAttribute("xeta", "XƏTA: Bu anbar " + istifadeciSayi + " istifadəçiyə təyin edilib! Əvvəlcə onların icazələrini dəyişin.");
                return "redirect:/layiheler";
            }

            // GƏLƏCƏKDƏ BURADA YOXLAMA OLACAQ:
            // long malSayi = mehsulRepo.countByAnbar_Id(id);
            // if (malSayi > 0) { ... "Anbarda mal var" ... }
            // long emeliyyatSayi = anbarEmeliyyatiRepo.countByAnbar_Id(id);
            // if (emeliyyatSayi > 0) { ... "Anbarda giriş/çıxış var" ... }

            anbarRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("ugur", "Anbar uğurla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/layiheler";
    }

    // ==========================================
    // KASSA HESABI ƏMƏLİYYATLARI
    // ==========================================
    @PostMapping("/kassa-hesabi-elave-et")
    public String kassaHesabiElaveEt(@RequestParam String ad) {
        if (ad != null && !ad.trim().isEmpty()) kassaHesabiRepo.save(new KassaHesabi(ad.trim()));
        return "redirect:/layiheler";
    }

    @GetMapping("/kassa-hesabi-redakte/{id}")
    public String kassaRedakteSehife(@PathVariable Long id, Model model) {
        model.addAttribute("kassa", kassaHesabiRepo.findById(id).orElseThrow());
        return "kassa-redakte";
    }

    @PostMapping("/kassa-hesabi-redakte/{id}")
    public String kassaRedakteEt(@PathVariable Long id, @RequestParam String ad) {
        KassaHesabi k = kassaHesabiRepo.findById(id).orElseThrow();
        k.setAd(ad.trim());
        kassaHesabiRepo.save(k);
        return "redirect:/layiheler";
    }

    @PostMapping("/kassa-hesabi-sil/{id}")
    public String kassaHesabiSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // YOXLAMA 1: İstifadəçiyə təyin edilibmi?
            long istifadeciSayi = istifadeciRepo.countByAllowedKassaHesablari_Id(id);
            if (istifadeciSayi > 0) {
                redirectAttributes.addFlashAttribute("xeta", "XƏTA: Bu kassa hesabı " + istifadeciSayi + " istifadəçiyə təyin edilib!");
                return "redirect:/layiheler";
            }

            // GƏLƏCƏKDƏ BURADA YOXLAMA OLACAQ:
            // long emeliyyatSayi = kassaEmeliyyatiRepo.countByKassaHesabi_Id(id);
            // if (emeliyyatSayi > 0) { ... "Kassada pul əməliyyatı var" ... }

            kassaHesabiRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("ugur", "Kassa hesabı uğurla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/layiheler";
    }

    // ==========================================
    // BANK HESABI ƏMƏLİYYATLARI
    // ==========================================
    @PostMapping("/bank-hesabi-elave-et")
    public String bankHesabiElaveEt(@RequestParam String ad) {
        if (ad != null && !ad.trim().isEmpty()) bankHesabiRepo.save(new BankHesabi(ad.trim()));
        return "redirect:/layiheler";
    }

    @GetMapping("/bank-hesabi-redakte/{id}")
    public String bankRedakteSehife(@PathVariable Long id, Model model) {
        model.addAttribute("bank", bankHesabiRepo.findById(id).orElseThrow());
        return "bank-redakte";
    }

    @PostMapping("/bank-hesabi-redakte/{id}")
    public String bankRedakteEt(@PathVariable Long id, @RequestParam String ad) {
        BankHesabi b = bankHesabiRepo.findById(id).orElseThrow();
        b.setAd(ad.trim());
        bankHesabiRepo.save(b);
        return "redirect:/layiheler";
    }

    @PostMapping("/bank-hesabi-sil/{id}")
    public String bankHesabiSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // YOXLAMA 1: İstifadəçiyə təyin edilibmi?
            long istifadeciSayi = istifadeciRepo.countByAllowedBankHesablari_Id(id);
            if (istifadeciSayi > 0) {
                redirectAttributes.addFlashAttribute("xeta", "XƏTA: Bu bank hesabı " + istifadeciSayi + " istifadəçiyə təyin edilib!");
                return "redirect:/layiheler";
            }

            // GƏLƏCƏKDƏ BURADA YOXLAMA OLACAQ:
            // long emeliyyatSayi = bankEmeliyyatiRepo.countByBankHesabi_Id(id);
            // if (emeliyyatSayi > 0) { ... "Bankda pul əməliyyatı var" ... }

            bankHesabiRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("ugur", "Bank hesabı uğurla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/layiheler";
    }
}