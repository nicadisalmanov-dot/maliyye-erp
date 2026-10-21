package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/bank")
public class BankController {

    @Autowired
    private BankService service;

    @Autowired
    private KontragentService kontragentService; // YENİ ƏLAVƏ

    @GetMapping
    public String bankSehife(Model model) {
        model.addAttribute("emeliyyatlar", service.hamisiniGetir());
        model.addAttribute("medaxil", service.medaxilCemi());
        model.addAttribute("mexaric", service.mexaricCemi());
        model.addAttribute("qaliq", service.bankQaligi());

        // YENİ ƏLAVƏ
        model.addAttribute("kontragentler", kontragentService.hamisiniGetir());

        return "bank";
    }

    @PostMapping("/elave-et")
    public String elaveEt(@RequestParam String tarix,
                          @RequestParam String senedNomresi,
                          @RequestParam String tesvir,
                          @RequestParam String mebleg,
                          @RequestParam String nov,
                          @RequestParam String bankHesabi,
                          @RequestParam(required = false) Long kontragentId) { // YENİ ƏLAVƏ

        BigDecimal meblegDecimal = new BigDecimal(mebleg);
        LocalDate tarixDecimal = LocalDate.parse(tarix);
        BankEmeliyyati yeni = new BankEmeliyyati(tarixDecimal, senedNomresi, tesvir, meblegDecimal, nov, bankHesabi);

        // YENİ ƏLAVƏ
        if (kontragentId != null) {
            Kontragent k = new Kontragent();
            k.setId(kontragentId);
            yeni.setKontragent(k);
        }

        service.elaveEt(yeni);
        return "redirect:/bank";
    }

    @PostMapping("/sil/{id}")
    public String sil(@PathVariable Long id) {
        service.sil(id);
        return "redirect:/bank";
    }
}