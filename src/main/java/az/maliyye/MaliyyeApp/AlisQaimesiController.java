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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/anbar/alish")
public class AlisQaimesiController {

    @Autowired private AlisQaimesiService service;
    @Autowired private AlisQaimesiRepository alisQaimesiRepo;
    @Autowired private AnbarRepository anbarRepo;
    @Autowired private LayiheRepository layiheRepo;
    @Autowired private KontragentService kontragentService;
    @Autowired private MehsulRepository mehsulRepo;
    @Autowired private XidmetRepository xidmetRepo;
    @Autowired private IstifadeciRepository istifadeciRepo;

    // Köməkçi metod: İcazəli anbarlar
    private List<Anbar> getIcazeliAnbarlar(String username) {
        Istifadeci user = istifadeciRepo.findByUsername(username);
        if (user != null && user.getRol().equals("ROLE_ADMIN")) return anbarRepo.findAll();
        return user != null ? user.getAllowedWarehouses() : new ArrayList<>();
    }

    // Köməkçi metod: İcazəli qaimələr
    private List<AlisQaimesi> getIcazeliQaimeler(String username, boolean arxiv) {
        Istifadeci user = istifadeciRepo.findByUsername(username);
        List<AlisQaimesi> butunQaimeler = arxiv ?
                alisQaimesiRepo.findBySilinibTrueOrderByIdDesc() :
                alisQaimesiRepo.findBySilinibFalseOrderByIdDesc();

        if (user != null && user.getRol().equals("ROLE_ADMIN")) return butunQaimeler;

        List<Anbar> icazeliAnbarlar = user != null ? user.getAllowedWarehouses() : new ArrayList<>();
        return butunQaimeler.stream()
                .filter(q -> icazeliAnbarlar.contains(q.getAnbar()))
                .collect(Collectors.toList());
    }

    @GetMapping
    public String alisSehife(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        model.addAttribute("qaimeler", getIcazeliQaimeler(username, false));
        model.addAttribute("anbarlar", getIcazeliAnbarlar(username));
        model.addAttribute("layiheler", layiheRepo.findAll());
        model.addAttribute("kontragentler", kontragentService.techizatciGetir());
        model.addAttribute("mehsullar", mehsulRepo.findAll());
        model.addAttribute("xidmetler", xidmetRepo.findAll());
        return "alis-qaimesi";
    }

    @PostMapping("/elave-et")
    public String alisEt(@RequestParam String tarix,
                         @RequestParam Long anbarId,
                         @RequestParam Long kontragentId,
                         @RequestParam(required = false) List<Long> mehsulIdler,
                         @RequestParam(required = false) List<Long> xidmetIdler,
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
            service.alisEt(tarixDate, anbarId, kontragentId, mehsulIdler, xidmetIdler, layiheIdler,
                    novler, miqdarlar, qiymetler, hesablar, hesablar2, qeydler);
            redirectAttributes.addFlashAttribute("ugur", "Alış qaiməsi uğurla yaradıldı!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/alish";
    }

    @GetMapping("/redakte/{id}")
    public String redakteSehife(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        AlisQaimesi qaime = alisQaimesiRepo.findById(id).orElseThrow();
        model.addAttribute("qaime", qaime);
        model.addAttribute("anbarlar", getIcazeliAnbarlar(username));
        model.addAttribute("layiheler", layiheRepo.findAll());
        model.addAttribute("kontragentler", kontragentService.techizatciGetir());
        model.addAttribute("mehsullar", mehsulRepo.findAll());
        model.addAttribute("xidmetler", xidmetRepo.findAll());
        return "alis-qaimesi-redakte";
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
                            @RequestParam(required = false) List<String> hesablar2,
                            @RequestParam(required = false) List<String> qeydler,
                            RedirectAttributes redirectAttributes) {
        try {
            LocalDate tarixDate = LocalDate.parse(tarix);
            service.qaimeRedakteEt(id, tarixDate, anbarId, kontragentId, mehsulIdler, xidmetIdler, layiheIdler,
                    novler, miqdarlar, qiymetler, hesablar, hesablar2, qeydler);
            redirectAttributes.addFlashAttribute("ugur", "Qaimə uğurla redaktə edildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/alish";
    }

    @GetMapping("/arxiv")
    public String arxivSehife(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // MÜVƏQQƏTİ: Birbaşa repository-dən bütün silinmişləri götürürük
        // Əgər bu işləsə, deməli problem getIcazeliQaimeler metodundadır
        List<AlisQaimesi> butunQaimeler = alisQaimesiRepo.findBySilinibTrueOrderByIdDesc();

        // Əgər istifadəçi admin deyilsə, yalnız onun anbarlarına aid olanları göstər
        Istifadeci user = istifadeciRepo.findByUsername(username);
        if (user != null && !user.getRol().equals("ROLE_ADMIN")) {
            List<Anbar> icazeliAnbarlar = user.getAllowedWarehouses();
            butunQaimeler = butunQaimeler.stream()
                    .filter(q -> icazeliAnbarlar.contains(q.getAnbar()))
                    .collect(java.util.stream.Collectors.toList());
        }

        model.addAttribute("qaimeler", butunQaimeler);
        return "alis-qaimesi-arxiv";
    }

    @GetMapping("/bax/{id}")
    public String qaimeBax(@PathVariable Long id, Model model) {
        AlisQaimesi qaime = alisQaimesiRepo.findById(id).orElseThrow();
        model.addAttribute("qaime", qaime);
        return "alis-qaimesi-bax";
    }

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

    @PostMapping("/berpa-et/{id}")
    public String qaimeBerpaEt(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.qaimeBerpaEt(id);
            redirectAttributes.addFlashAttribute("ugur", "Qaimə uğurla bərpa edildi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/alish/arxiv";
    }

    @PostMapping("/birdəfəlik-sil/{id}")
    public String qaimeBirdəfəlikSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.qaimeBirdəfəlikSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Qaimə birdəfəlik silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/alish/arxiv";
    }
}