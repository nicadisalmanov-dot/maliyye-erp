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
@RequestMapping("/anbar/satis")
public class SatisQaimesiController {

    @Autowired private SatisQaimesiService service;
    @Autowired private SatisQaimesiRepository satisRepo;
    @Autowired private AnbarRepository anbarRepo;
    @Autowired private LayiheRepository layiheRepo;
    @Autowired private KontragentService kontragentService; // YENİ: Service ilə əvəz etdik
    @Autowired private MehsulRepository mehsulRepo;
    @Autowired private XidmetRepository xidmetRepo;

    @GetMapping
    public String satisSehife(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        model.addAttribute("qaimeler", service.qaimeleriGetir(username));
        model.addAttribute("anbarlar", anbarRepo.findAll());
        model.addAttribute("layiheler", layiheRepo.findAll());
        // YENİ: Yalnız müştəriləri göstəririk
        model.addAttribute("kontragentler", kontragentService.musteriGetir());
        model.addAttribute("mehsullar", mehsulRepo.findAll());
        model.addAttribute("xidmetler", xidmetRepo.findAll());
        return "satis-qaimesi";
    }

    @PostMapping("/elave-et")
    public String satisEt(@RequestParam String tarix,
                          @RequestParam Long anbarId,
                          @RequestParam Long kontragentId,
                          @RequestParam(required = false) List<Long> mehsulIdler,
                          @RequestParam(required = false) List<Long> xidmetIdler,
                          @RequestParam(required = false) List<Long> layiheIdler,
                          @RequestParam List<String> novler,
                          @RequestParam List<BigDecimal> miqdarlar,
                          @RequestParam List<BigDecimal> qiymetler,
                          @RequestParam(required = false) List<String> hesablar,
                          @RequestParam(required = false) List<String> qeydler,
                          RedirectAttributes redirectAttributes) {
        try {
            LocalDate tarixDate = LocalDate.parse(tarix);
            service.satisEt(tarixDate, anbarId, kontragentId, mehsulIdler, xidmetIdler, layiheIdler,
                    novler, miqdarlar, qiymetler, hesablar, qeydler);
            redirectAttributes.addFlashAttribute("ugur", "Satış qaiməsi uğurla yaradıldı!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/satis";
    }

    @GetMapping("/redakte/{id}")
    public String redakteSehife(@PathVariable Long id, Model model) {
        SatisQaimesi qaime = satisRepo.findById(id).orElseThrow();
        model.addAttribute("qaime", qaime);
        model.addAttribute("anbarlar", anbarRepo.findAll());
        model.addAttribute("layiheler", layiheRepo.findAll());
        model.addAttribute("kontragentler", kontragentService.musteriGetir()); // YENİ
        model.addAttribute("mehsullar", mehsulRepo.findAll());
        model.addAttribute("xidmetler", xidmetRepo.findAll());
        return "satis-qaimesi-redakte";
    }

    @PostMapping("/redakte/{id}")
    public String redakteEt(@PathVariable Long id,
                            @RequestParam String tarix,
                            @RequestParam Long anbarId,
                            @RequestParam Long kontragentId,
                            @RequestParam(required = false) List<Long> mehsulIdler,
                            @RequestParam(required = false) List<Long> xidmetIdler,
                            @RequestParam(required = false) List<Long> layiheIdler,
                            @RequestParam List<String> novler,
                            @RequestParam List<BigDecimal> miqdarlar,
                            @RequestParam List<BigDecimal> qiymetler,
                            @RequestParam(required = false) List<String> hesablar,
                            @RequestParam(required = false) List<String> qeydler,
                            RedirectAttributes redirectAttributes) {
        try {
            LocalDate tarixDate = LocalDate.parse(tarix);
            service.qaimeRedakteEt(id, tarixDate, anbarId, kontragentId, mehsulIdler, xidmetIdler, layiheIdler,
                    novler, miqdarlar, qiymetler, hesablar, qeydler);
            redirectAttributes.addFlashAttribute("ugur", "Satış qaiməsi uğurla redaktə edildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/satis";
    }

    @GetMapping("/arxiv")
    public String arxivSehife(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("qaimeler", service.arxivQaimeleriGetir(username));
        return "satis-qaimesi-arxiv";
    }

    @PostMapping("/sil/{id}")
    public String qaimeSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.qaimeSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Satış qaiməsi silindi (Arxivə köçürüldü).");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/satis";
    }

    @PostMapping("/berpa-et/{id}")
    public String qaimeBerpaEt(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.qaimeBerpaEt(id);
            redirectAttributes.addFlashAttribute("ugur", "Satış qaiməsi uğurla bərpa edildi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/satis/arxiv";
    }
}