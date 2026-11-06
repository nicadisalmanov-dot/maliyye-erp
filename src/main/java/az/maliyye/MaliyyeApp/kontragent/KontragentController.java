package az.maliyye.MaliyyeApp.kontragent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/kontragentler")
public class KontragentController {

    @Autowired
    private KontragentRepository kontragentRepository;

    // ================= ANA SƏHİFƏ =================
    @GetMapping({"", "/"})
    public String kontragentlerMerkez() {
        return "kontragent/kontragentler";
    }

    // ================= DEBİTORLAR =================
    @GetMapping("/debitorlar")
    public String debitorlar(Model model) {
        // Bazadan yalnız Müştəriləri (Debitorları) çəkirik
        List<Kontragent> debitorlar = kontragentRepository.findByKontragentNovu("MUSTERI");
        model.addAttribute("siyahilik", debitorlar);
        return "kontragent/debitorlar";
    }

    @PostMapping("/debitorlar/elave-et")
    public String debitorElaveEt(@RequestParam String subyektNovu,
                                 @RequestParam(required = false) String adSoyad,
                                 @RequestParam(required = false) String finKodu,
                                 @RequestParam(required = false) String vesiqeNomresi,
                                 @RequestParam(required = false) String sirketAdi,
                                 @RequestParam(required = false) String voen,
                                 @RequestParam(required = false) String telefon,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) String unvan,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            Kontragent k = new Kontragent();
            k.setSubyektNovu(subyektNovu);
            k.setKontragentNovu("MUSTERI"); // Debitor olduğu üçün

            if ("FIZIKI".equals(subyektNovu)) {
                k.setAdSoyad(adSoyad);
                k.setFinKodu(finKodu);
                k.setVesiqeNomresi(vesiqeNomresi);
            } else {
                k.setSirketAdi(sirketAdi);
                k.setVoen(voen);
            }
            k.setTelefon(telefon);
            k.setEmail(email);
            k.setUnvan(unvan);

            k.setYaradanIstifadeci(principal != null ? principal.getName() : "Admin");
            k.setYaradilmaTarixi(LocalDateTime.now());

            kontragentRepository.save(k);
            redirectAttributes.addFlashAttribute("ugur", "Debitor uğurla əlavə edildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/kontragentler/debitorlar";
    }

    @PostMapping("/debitorlar/sil/{id}")
    public String debitorSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            kontragentRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("ugur", "Debitor silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/kontragentler/debitorlar";
    }

    // ================= KREDİTORLAR =================
    @GetMapping("/kreditorlar")
    public String kreditorlar(Model model) {
        List<Kontragent> kreditorlar = kontragentRepository.findByKontragentNovu("TECHIZATCI");
        model.addAttribute("siyahilik", kreditorlar);
        return "kontragent/kreditorlar";
    }

    @PostMapping("/kreditorlar/elave-et")
    public String kreditorElaveEt(@RequestParam String subyektNovu,
                                  @RequestParam(required = false) String adSoyad,
                                  @RequestParam(required = false) String finKodu,
                                  @RequestParam(required = false) String vesiqeNomresi,
                                  @RequestParam(required = false) String sirketAdi,
                                  @RequestParam(required = false) String voen,
                                  @RequestParam(required = false) String telefon,
                                  @RequestParam(required = false) String email,
                                  @RequestParam(required = false) String unvan,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        try {
            Kontragent k = new Kontragent();
            k.setSubyektNovu(subyektNovu);
            k.setKontragentNovu("TECHIZATCI");

            if ("FIZIKI".equals(subyektNovu)) {
                k.setAdSoyad(adSoyad);
                k.setFinKodu(finKodu);
                k.setVesiqeNomresi(vesiqeNomresi);
            } else {
                k.setSirketAdi(sirketAdi);
                k.setVoen(voen);
            }
            k.setTelefon(telefon);
            k.setEmail(email);
            k.setUnvan(unvan);

            k.setYaradanIstifadeci(principal != null ? principal.getName() : "Admin");
            k.setYaradilmaTarixi(LocalDateTime.now());

            kontragentRepository.save(k);
            redirectAttributes.addFlashAttribute("ugur", "Kreditor uğurla əlavə edildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/kontragentler/kreditorlar";
    }

    @PostMapping("/kreditorlar/sil/{id}")
    public String kreditorSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            kontragentRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("ugur", "Kreditor silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/kontragentler/kreditorlar";
    }

    // ================= HESABAT MƏRKƏZİ =================
    @GetMapping("/hesabat")
    public String hesabatMerkezi() {
        return "kontragent/hesabat";
    }

    // ================= BORC QALIQ CƏDVƏLİ =================
    @GetMapping("/hesabat/borc-qaliq")
    public String borcQaliqHesabati(
            @RequestParam(required = false) String baslangic,
            @RequestParam(required = false) String son,
            Model model) {

        // Hələlik boş siyahılar göndəririk (sonra bura real hesablama məntiqi yazılacaq)
        model.addAttribute("debitorlar", new ArrayList<>());
        model.addAttribute("kreditorlar", new ArrayList<>());
        model.addAttribute("baslangic", baslangic);
        model.addAttribute("son", son);

        return "kontragent/hesabat-borc-qaliq";
    }

    // ================= DİGƏR HESABATLAR (Müvəqqəti) =================
    @GetMapping("/hesabat/debitor-dovriyye")
    public String debitorDovriyyeHesabati() {
        return "kontragent/hesabat-debitor-dovriyye";
    }

    @GetMapping("/hesabat/kreditor-ohdelikleri")
    public String kreditorOhdelikleriHesabati() {
        return "kontragent/hesabat-kreditor-ohdelikleri";
    }

    @GetMapping("/hesabat/borc-muqavileleri-icmal")
    public String borcMuqavileleriIcmal() {
        return "kontragent/hesabat-borc-muqavileleri";
    }

    @GetMapping("/hesabat/tehtelhesab-qaliq")
    public String tehtelhesabQaliq() {
        return "kontragent/hesabat-tehtelhesab";
    }

    // ================= TƏHTƏLHESAB (Redirect) =================
    @GetMapping("/tehtelhesab")
    public String tehtelhesab() {
        return "redirect:/tehtelhesab";
    }
}