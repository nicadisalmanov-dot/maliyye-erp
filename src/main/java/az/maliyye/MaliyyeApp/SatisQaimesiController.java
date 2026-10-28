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
@RequestMapping("/anbar/satis")
public class SatisQaimesiController {

    @Autowired private SatisQaimesiService service;
    @Autowired private SatisQaimesiRepository satisRepo;
    @Autowired private AnbarRepository anbarRepo;
    @Autowired private LayiheRepository layiheRepo;
    @Autowired private KontragentService kontragentService;
    @Autowired private MehsulRepository mehsulRepo;
    @Autowired private XidmetRepository xidmetRepo;
    @Autowired private IstifadeciRepository istifadeciRepo;
    @Autowired private AnbarMehsulRepository anbarMehsulRepo; // YENİ: Anbar qalıqları üçün

    // Köməkçi metod: İcazəli anbarlar
    private List<Anbar> getIcazeliAnbarlar(String username) {
        Istifadeci user = istifadeciRepo.findByUsername(username);
        if (user != null && user.getRol().equals("ROLE_ADMIN")) return anbarRepo.findAll();
        return user != null ? user.getAllowedWarehouses() : new ArrayList<>();
    }

    // Köməkçi metod: İcazəli qaimələr
    private List<SatisQaimesi> getIcazeliQaimeler(String username) {
        Istifadeci user = istifadeciRepo.findByUsername(username);
        List<SatisQaimesi> butunQaimeler = satisRepo.findBySilinibFalseOrderByIdDesc();

        if (user != null && user.getRol().equals("ROLE_ADMIN")) return butunQaimeler;

        List<Anbar> icazeliAnbarlar = user != null ? user.getAllowedWarehouses() : new ArrayList<>();
        return butunQaimeler.stream()
                .filter(q -> icazeliAnbarlar.contains(q.getAnbar()))
                .collect(Collectors.toList());
    }

    @GetMapping
    public String satisSehife(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        model.addAttribute("qaimeler", getIcazeliQaimeler(username));
        model.addAttribute("anbarlar", getIcazeliAnbarlar(username));
        model.addAttribute("layiheler", layiheRepo.findAll());
        model.addAttribute("kontragentler", kontragentService.musteriGetir());
        model.addAttribute("mehsullar", mehsulRepo.findAll());
        model.addAttribute("xidmetler", xidmetRepo.findAll());

        // YENİ: Bütün anbar qalıqlarını JavaScript üçün ötürürük
        List<AnbarMehsul> anbarMehsullar = anbarMehsulRepo.findAll();
        model.addAttribute("anbarMehsullar", anbarMehsullar);

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        SatisQaimesi qaime = satisRepo.findById(id).orElseThrow();
        model.addAttribute("qaime", qaime);
        model.addAttribute("anbarlar", getIcazeliAnbarlar(username));
        model.addAttribute("layiheler", layiheRepo.findAll());
        model.addAttribute("kontragentler", kontragentService.musteriGetir());
        model.addAttribute("mehsullar", mehsulRepo.findAll());
        model.addAttribute("xidmetler", xidmetRepo.findAll());

        // YENİ: Bütün anbar qalıqlarını JavaScript üçün ötürürük
        List<AnbarMehsul> anbarMehsullar = anbarMehsulRepo.findAll();
        model.addAttribute("anbarMehsullar", anbarMehsullar);

        return "satis-qaimesi-redakte";
    }

    @GetMapping("/arxiv")
    public String arxivSehife(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Istifadeci user = istifadeciRepo.findByUsername(username);

        List<SatisQaimesi> butunQaimeler = satisRepo.findBySilinibTrueOrderByIdDesc();
        if (user != null && !user.getRol().equals("ROLE_ADMIN")) {
            List<Anbar> icazeliAnbarlar = user.getAllowedWarehouses();
            butunQaimeler = butunQaimeler.stream()
                    .filter(q -> icazeliAnbarlar.contains(q.getAnbar()))
                    .collect(Collectors.toList());
        }
        model.addAttribute("qaimeler", butunQaimeler);
        return "satis-qaimesi-arxiv";
    }

    @GetMapping("/bax/{id}")
    public String qaimeBax(@PathVariable Long id, Model model) {
        SatisQaimesi qaime = satisRepo.findById(id).orElseThrow();
        model.addAttribute("qaime", qaime);
        return "satis-qaimesi-bax";
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

    @PostMapping("/birdəfəlik-sil/{id}")
    public String qaimeBirdəfəlikSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.qaimeBirdəfəlikSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Satış qaiməsi birdəfəlik silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/satis/arxiv";
    }
}