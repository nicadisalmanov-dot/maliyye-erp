package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/kassa")
public class KassaController {

    @Autowired
    private KassaService service;

    @Autowired
    private KontragentService kontragentService;

    @GetMapping
    public String kassaSehife(@RequestParam(required = false) String axtaris,
                              @RequestParam(required = false) String ay,
                              @RequestParam(required = false) String nov,
                              Model model) {

        List<KassaEmeliyyati> emeliyyatlar = service.filterle(axtaris, ay, nov);

        model.addAttribute("emeliyyatlar", emeliyyatlar);
        model.addAttribute("medaxil", service.medaxilCemi(emeliyyatlar));
        model.addAttribute("mexaric", service.mexaricCemi(emeliyyatlar));
        model.addAttribute("qaliq", service.kassaQaligi(emeliyyatlar));

        model.addAttribute("axtaris", axtaris);
        model.addAttribute("ay", ay);
        model.addAttribute("nov", nov);

        // YENİ ƏLAVƏ: HTML-ə kontragent siyahısını göndürürük
        model.addAttribute("kontragentler", kontragentService.hamisiniGetir());

        return "kassa";
    }

    @PostMapping("/elave-et")
    public String elaveEt(@RequestParam String senedNomresi,
                          @RequestParam String tesvir,
                          @RequestParam String mebleg,
                          @RequestParam String nov,
                          @RequestParam String tarix,
                          @RequestParam(required = false) Long kontragentId) {

        BigDecimal meblegDecimal = new BigDecimal(mebleg);
        LocalDate tarixDecimal = LocalDate.parse(tarix);
        KassaEmeliyyati yeni = new KassaEmeliyyati(tarixDecimal, senedNomresi, tesvir, meblegDecimal, nov);

        // YENİ ƏLAVƏ: Kontragent seçilibsə əlavə edirik
        if (kontragentId != null) {
            Kontragent k = new Kontragent();
            k.setId(kontragentId);
            yeni.setKontragent(k);
        }

        service.elaveEt(yeni);
        return "redirect:/kassa";
    }

    @PostMapping("/sil/{id}")
    public String sil(@PathVariable Long id) {
        service.sil(id);
        return "redirect:/kassa";
    }
}