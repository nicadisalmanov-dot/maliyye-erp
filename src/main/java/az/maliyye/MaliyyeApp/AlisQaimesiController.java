package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/anbar/alish")
public class AlisQaimesiController {

    @Autowired private AlisQaimesiService service;
    @Autowired private AlisQaimesiRepository alisQaimesiRepo;
    @Autowired private AnbarRepository anbarRepo;
    @Autowired private LayiheRepository layiheRepo;
    @Autowired private KontragentService kontragentService; // YENİ: Service ilə əvəz etdik
    @Autowired private MehsulRepository mehsulRepo;

    // 1. Əsas səhifə (Aktiv qaimələr - istifadəçiyə görə filtrli)
    @GetMapping
    public String alisSehife(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        model.addAttribute("qaimeler", service.qaimeleriGetir(username));
        model.addAttribute("anbarlar", anbarRepo.findAll());
        model.addAttribute("layiheler", layiheRepo.findAll());
        // YENİ: Yalnız təchizatçıları göstəririk
        model.addAttribute("kontragentler", kontragentService.techizatciGetir());
        model.addAttribute("mehsullar", mehsulRepo.findAll());
        return "alis-qaimesi";
    }

    // 2. Yeni qaimə yaratmaq
    @PostMapping("/elave-et")
    public String alisEt(@RequestParam String tarix,
                         @RequestParam Long anbarId,
                         @RequestParam Long kontragentId,
                         @RequestParam(required = false) List<Long> mehsulIdler,
                         @RequestParam(required = false) List<String> xidmetAdlari,
                         @RequestParam(required = false) List<Long> layiheIdler,
                         @RequestParam List<String> novler,
                         @RequestParam List<BigDecimal> miqdarlar,
                         @RequestParam List<BigDecimal> qiymetler,
                         @RequestParam(required = false) List<String> hesablar,
                         @RequestParam(required = false) List<String> hesablar2,
                         @RequestParam(required = false) List<String> qeydler,
                         RedirectAttributes redirectAttributes) {
        try {
            LocalDate tarixDate = LocalDate.parse(tarix);
            service.alisEt(tarixDate, anbarId, kontragentId, mehsulIdler, xidmetAdlari, layiheIdler,
                    novler, miqdarlar, qiymetler, hesablar, hesablar2, qeydler);
            redirectAttributes.addFlashAttribute("ugur", "Alış qaiməsi uğurla yaradıldı!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/alish";
    }

    // 3. Redaktə səhifəsini göstər
    @GetMapping("/redakte/{id}")
    public String redakteSehife(@PathVariable Long id, Model model) {
        AlisQaimesi qaime = alisQaimesiRepo.findById(id).orElseThrow();
        model.addAttribute("qaime", qaime);
        model.addAttribute("anbarlar", anbarRepo.findAll());
        model.addAttribute("layiheler", layiheRepo.findAll());
        // YENİ: Yalnız təchizatçıları göstəririk
        model.addAttribute("kontragentler", kontragentService.techizatciGetir());
        model.addAttribute("mehsullar", mehsulRepo.findAll());
        return "alis-qaimesi-redakte";
    }

    // 4. Redaktəni yadda saxla
    @PostMapping("/redakte/{id}")
    public String redakteEt(@PathVariable Long id,
                            @RequestParam String tarix,
                            @RequestParam Long anbarId,
                            @RequestParam Long kontragentId,
                            @RequestParam(required = false) List<Long> mehsulIdler,
                            @RequestParam(required = false) List<String> xidmetAdlari,
                            @RequestParam(required = false) List<Long> layiheIdler,
                            @RequestParam List<String> novler,
                            @RequestParam List<BigDecimal> miqdarlar,
                            @RequestParam List<BigDecimal> qiymetler,
                            @RequestParam(required = false) List<String> hesablar,
                            @RequestParam(required = false) List<String> hesablar2,
                            @RequestParam(required = false) List<String> qeydler,
                            RedirectAttributes redirectAttributes) {
        try {
            LocalDate tarixDate = LocalDate.parse(tarix);
            service.qaimeRedakteEt(id, tarixDate, anbarId, kontragentId, mehsulIdler, xidmetAdlari, layiheIdler,
                    novler, miqdarlar, qiymetler, hesablar, hesablar2, qeydler);
            redirectAttributes.addFlashAttribute("ugur", "Qaimə uğurla redaktə edildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/alish";
    }

    // 5. Arxiv səhifəsi
    @GetMapping("/arxiv")
    public String arxivSehife(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("qaimeler", service.arxivQaimeleriGetir(username));
        return "alis-qaimesi-arxiv";
    }

    // 6. Silmə (Arxivə köçürmə)
    @PostMapping("/sil/{id}")
    public String qaimeSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.qaimeSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Qaimə silindi (Arxivə köçürüldü).");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/alish";
    }

    // 7. Bərpa et
    @PostMapping("/berpa-et/{id}")
    public String qaimeBerpaEt(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.qaimeBerpaEt(id);
            redirectAttributes.addFlashAttribute("ugur", "Qaimə uğurla bərpa edildi və aktiv siyahıya köçürüldü.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/alish/arxiv";
    }
}