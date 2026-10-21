package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/istifadeciler")
public class IstifadeciController {

    @Autowired
    private IstifadeciRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String istifadeciSehife(Model model) {
        model.addAttribute("istifadeciler", repo.findAll());
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
                          @RequestParam(required = false) Boolean hesabat) {

        Istifadeci yeni = new Istifadeci();
        yeni.setUsername(username);
        yeni.setPassword(passwordEncoder.encode(password));
        yeni.setAd(ad);

        // BURADA DƏYİŞİKLİK EDİRİK: Rolun qarşısına ROLE_ əlavə edirik
        yeni.setRol("ROLE_" + rol);

        yeni.setIcazeKassa(kassa != null);
        yeni.setIcazeBank(bank != null);
        yeni.setIcazeKontragent(kontragent != null);
        yeni.setIcazeAnbar(anbar != null);
        yeni.setIcazeSatis(satis != null);
        yeni.setIcazeSatinalma(satinalma != null);
        yeni.setIcazeHesabat(hesabat != null);

        repo.save(yeni);
        return "redirect:/istifadeciler";
    }

    @PostMapping("/sil/{id}")
    public String sil(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/istifadeciler";
    }
}