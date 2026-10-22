package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/istifadeciler")
public class IstifadeciController {

    @Autowired
    private IstifadeciRepository repo;

    @Autowired
    private LayiheRepository layiheRepo;

    @Autowired
    private AnbarRepository anbarRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String istifadeciSehife(Model model) {
        model.addAttribute("istifadeciler", repo.findAll());
        // Layihə və Anbarları HTML-ə göndəririk
        model.addAttribute("layiheler", layiheRepo.findAll());
        model.addAttribute("anbarlar", anbarRepo.findAll());
        return "istifadeciler";
    }

    @PostMapping("/elave-et")
    public String elaveEt(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String ad,
                          @RequestParam String rol,
                          @RequestParam(required = false) Boolean kassa,
                          @RequestParam(required = false) Boolean bank,
                          @RequestParam(required = false) Boolean kontragent,
                          @RequestParam(required = false) Boolean anbar,
                          @RequestParam(required = false) Boolean satis,
                          @RequestParam(required = false) Boolean satinalma,
                          @RequestParam(required = false) Boolean hesabat,
                          @RequestParam(required = false) List<Long> layiheler,
                          @RequestParam(required = false) List<Long> anbarlar) {

        Istifadeci yeni = new Istifadeci();
        yeni.setUsername(username);
        yeni.setPassword(passwordEncoder.encode(password));
        yeni.setAd(ad);
        yeni.setRol("ROLE_" + rol);

        yeni.setIcazeKassa(kassa != null);
        yeni.setIcazeBank(bank != null);
        yeni.setIcazeKontragent(kontragent != null);
        yeni.setIcazeAnbar(anbar != null);
        yeni.setIcazeSatis(satis != null);
        yeni.setIcazeSatinalma(satinalma != null);
        yeni.setIcazeHesabat(hesabat != null);

        // Seçilmiş Layihələri və Anbarları əlavə edirik
        if (layiheler != null) {
            yeni.setAllowedProjects(layiheRepo.findAllById(layiheler));
        }
        if (anbarlar != null) {
            yeni.setAllowedWarehouses(anbarRepo.findAllById(anbarlar));
        }

        repo.save(yeni);
        return "redirect:/istifadeciler";
    }
    // Redaktə səhifəsini göstər
    @GetMapping("/redakte/{id}")
    public String redakteSehife(@PathVariable Long id, Model model) {
        Istifadeci istifadeci = repo.findById(id).orElseThrow();
        model.addAttribute("istifadeci", istifadeci);
        model.addAttribute("layiheler", layiheRepo.findAll());
        model.addAttribute("anbarlar", anbarRepo.findAll());
        return "istifadeci-redakte";
    }

    // Redaktəni yadda saxla
    @PostMapping("/redakte/{id}")
    public String redakteEt(@PathVariable Long id,
                            @RequestParam String ad,
                            @RequestParam String rol,
                            @RequestParam(required = false) Boolean kassa,
                            @RequestParam(required = false) Boolean bank,
                            @RequestParam(required = false) Boolean kontragent,
                            @RequestParam(required = false) Boolean anbar,
                            @RequestParam(required = false) Boolean satis,
                            @RequestParam(required = false) Boolean satinalma,
                            @RequestParam(required = false) Boolean hesabat,
                            @RequestParam(required = false) List<Long> layiheler,
                            @RequestParam(required = false) List<Long> anbarlar) {

        Istifadeci istifadeci = repo.findById(id).orElseThrow();
        istifadeci.setAd(ad);
        istifadeci.setRol("ROLE_" + rol);

        istifadeci.setIcazeKassa(kassa != null);
        istifadeci.setIcazeBank(bank != null);
        istifadeci.setIcazeKontragent(kontragent != null);
        istifadeci.setIcazeAnbar(anbar != null);
        istifadeci.setIcazeSatis(satis != null);
        istifadeci.setIcazeSatinalma(satinalma != null);
        istifadeci.setIcazeHesabat(hesabat != null);

        if (layiheler != null) {
            istifadeci.setAllowedProjects(layiheRepo.findAllById(layiheler));
        } else {
            istifadeci.setAllowedProjects(null);
        }

        if (anbarlar != null) {
            istifadeci.setAllowedWarehouses(anbarRepo.findAllById(anbarlar));
        } else {
            istifadeci.setAllowedWarehouses(null);
        }

        repo.save(istifadeci);
        return "redirect:/istifadeciler";
    }
    @PostMapping("/sil/{id}")
    public String sil(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/istifadeciler";
    }
}