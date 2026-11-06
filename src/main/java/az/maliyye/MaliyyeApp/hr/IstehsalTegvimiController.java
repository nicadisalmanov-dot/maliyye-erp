package az.maliyye.MaliyyeApp.hr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/hr/teqvim")
public class IstehsalTegvimiController {

    @Autowired private IstehsalTegvimiService teqvimService;

    @GetMapping
    public String teqvim(@RequestParam(required = false) Integer il,
                         @RequestParam(required = false) Integer ay,
                         Model model) {
        int cariIl = LocalDate.now().getYear();
        int cariAy = LocalDate.now().getMonthValue();
        int secilmisIl = (il != null) ? il : cariIl;
        int secilmisAy = (ay != null) ? ay : cariAy;

        List<IstehsalTegvimi> gunler = teqvimService.ayinTegvimi(secilmisIl, secilmisAy);

        model.addAttribute("gunler", gunler);
        model.addAttribute("secilmisIl", secilmisIl);
        model.addAttribute("secilmisAy", secilmisAy);
        model.addAttribute("cariIl", cariIl);
        return "hr/teqvim";
    }

    @PostMapping("/status-deyis")
    public String statusDeyis(@RequestParam String tarix,
                              @RequestParam String status,
                              @RequestParam(required = false) String qeyd,
                              RedirectAttributes ra) {
        try {
            LocalDate d = LocalDate.parse(tarix);
            teqvimService.gunStatusunuDeyis(d, status, qeyd);
            ra.addFlashAttribute("ugur", "Təqvim yeniləndi: " + d);
        } catch (Exception e) {
            ra.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/hr/teqvim";
    }

    @PostMapping("/ayin-hamisi-ish")
    public String ayinHamisiIsh(@RequestParam int il,
                                @RequestParam int ay,
                                RedirectAttributes ra) {
        try {
            teqvimService.ayinTegviminiYarat(il, ay);
            // Bütün həftəsonu və bayramları İŞ GÜNÜ etmək (əgər istifadəçi istəsə)
            ra.addFlashAttribute("ugur", "Təqvim yaradıldı. İndi fərdi günləri dəyişə bilərsiniz.");
        } catch (Exception e) {
            ra.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/hr/teqvim?il=" + il + "&ay=" + ay;
    }
}