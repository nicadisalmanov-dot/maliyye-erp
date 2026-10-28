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
@RequestMapping("/anbar/alis-qaytarma")
public class AlisdanGeriQaytarmaController {

    @Autowired private AlisdanGeriQaytarmaService service;
    @Autowired private AlisdanGeriQaytarmaRepository geriQaytarmaRepo;
    @Autowired private LayiheRepository layiheRepo;
    @Autowired private KontragentService kontragentService;
    @Autowired private MehsulRepository mehsulRepo;
    @Autowired private XidmetRepository xidmetRepo;

    @GetMapping
    public String qaytarmaSehife(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        model.addAttribute("qaimeler", service.qaimeleriGetir(username));
        model.addAttribute("anbarlar", service.getIcazeliAnbarlar(username));
        model.addAttribute("layiheler", layiheRepo.findAll());
        model.addAttribute("kontragentler", kontragentService.techizatciGetir());
        model.addAttribute("mehsullar", mehsulRepo.findAll());
        model.addAttribute("xidmetler", xidmetRepo.findAll());
        return "alisdan-geri-qaytarma";
    }

    @PostMapping("/elave-et")
    public String qaytarmaEt(@RequestParam String tarix,
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
            service.geriQaytarmaEt(tarixDate, anbarId, kontragentId, mehsulIdler, xidmetIdler, layiheIdler,
                    novler, miqdarlar, qiymetler, hesablar, qeydler);
            redirectAttributes.addFlashAttribute("ugur", "Malın geri qaytarılması qaiməsi uğurla yaradıldı!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/alis-qaytarma";
    }

    @GetMapping("/bax/{id}")
    public String qaimeBax(@PathVariable Long id, Model model) {
        AlisdanGeriQaytarma qaime = geriQaytarmaRepo.findById(id).orElseThrow();
        model.addAttribute("qaime", qaime);
        return "alisdan-geri-qaytarma-bax";
    }

    @GetMapping("/redakte/{id}")
    public String redakteSehife(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        AlisdanGeriQaytarma qaime = geriQaytarmaRepo.findById(id).orElseThrow();
        model.addAttribute("qaime", qaime);
        model.addAttribute("anbarlar", service.getIcazeliAnbarlar(username));
        model.addAttribute("layiheler", layiheRepo.findAll());
        model.addAttribute("kontragentler", kontragentService.techizatciGetir());
        model.addAttribute("mehsullar", mehsulRepo.findAll());
        model.addAttribute("xidmetler", xidmetRepo.findAll());
        return "alisdan-geri-qaytarma-redakte";
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
            service.geriQaytarmaRedakteEt(id, tarixDate, anbarId, kontragentId, mehsulIdler, xidmetIdler, layiheIdler,
                    novler, miqdarlar, qiymetler, hesablar, qeydler);
            redirectAttributes.addFlashAttribute("ugur", "Geri qaytarma qaiməsi uğurla redaktə edildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/alis-qaytarma";
    }

    @GetMapping("/arxiv")
    public String arxivSehife(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        model.addAttribute("qaimeler", service.arxivQaimeleriGetir(username));
        return "alisdan-geri-qaytarma-arxiv";
    }

    @PostMapping("/sil/{id}")
    public String qaimeSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.qaimeSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Qaimə arxivə köçürüldü.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/alis-qaytarma";
    }

    @PostMapping("/berpa-et/{id}")
    public String qaimeBerpaEt(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.qaimeBerpaEt(id);
            redirectAttributes.addFlashAttribute("ugur", "Qaimə uğurla bərpa edildi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/alis-qaytarma/arxiv";
    }

    @PostMapping("/birdəfəlik-sil/{id}")
    public String qaimeBirdəfəlikSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.qaimeBirdəfəlikSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Qaimə birdəfəlik silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/alis-qaytarma/arxiv";
    }
}