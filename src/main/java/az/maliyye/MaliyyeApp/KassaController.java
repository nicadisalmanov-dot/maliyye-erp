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
@RequestMapping("/kassa")
public class KassaController {

    @Autowired private KassaService service;
    @Autowired private KontragentService kontragentService;
    @Autowired private KassaHesabiRepository kassaHesabiRepo;
    @Autowired private LayiheRepository layiheRepo;
    @Autowired private IstifadeciRepository istifadeciRepo;

    @GetMapping
    public String kassaSehife(@RequestParam(required = false) String axtaris,
                              @RequestParam(required = false) String ay,
                              @RequestParam(required = false) String nov,
                              @RequestParam(required = false) Boolean tesdiq,
                              Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Istifadeci user = istifadeciRepo.findByUsername(username);
        boolean isAdmin = user != null && user.getRol().equals("ROLE_ADMIN");

        List<KassaEmeliyyati> butunEmeliyyatlar = service.filterle(axtaris, ay, nov, tesdiq);

        // İcazə filtri
        if (!isAdmin && user != null && user.getAllowedKassaHesablari() != null) {
            List<Long> icazeli = user.getAllowedKassaHesablari().stream()
                    .map(KassaHesabi::getId).collect(Collectors.toList());
            butunEmeliyyatlar = butunEmeliyyatlar.stream()
                    .filter(e -> e.getKassaHesabi() != null && icazeli.contains(e.getKassaHesabi().getId()))
                    .collect(Collectors.toList());
        }
        if (!isAdmin && (user == null || user.getAllowedKassaHesablari() == null || user.getAllowedKassaHesablari().isEmpty())) {
            butunEmeliyyatlar = new ArrayList<>();
        }

        model.addAttribute("emeliyyatlar", butunEmeliyyatlar);
        model.addAttribute("medaxil", service.medaxilCemi(butunEmeliyyatlar));
        model.addAttribute("mexaric", service.mexaricCemi(butunEmeliyyatlar));
        model.addAttribute("qaliq", service.kassaQaligi(butunEmeliyyatlar));

        model.addAttribute("axtaris", axtaris);
        model.addAttribute("ay", ay);
        model.addAttribute("nov", nov);
        model.addAttribute("tesdiq", tesdiq);

        // Kassa hesabları
        List<KassaHesabi> gosterilecek;
        if (isAdmin) {
            gosterilecek = kassaHesabiRepo.findAll();
        } else if (user != null && user.getAllowedKassaHesablari() != null) {
            gosterilecek = user.getAllowedKassaHesablari();
        } else {
            gosterilecek = new ArrayList<>();
        }
        model.addAttribute("kassaHesablari", gosterilecek);
        model.addAttribute("kontragentler", kontragentService.hamisiniGetir());
        model.addAttribute("layiheler", layiheRepo.findAll());

        // YENİ: Kassa qalıqları (hesab üzrə)
        model.addAttribute("kassaQaliqlari", service.kassaHesablariQaliqlari(username));

        return "kassa";
    }

    @PostMapping("/elave-et")
    public String elaveEt(@RequestParam String tarix,
                          @RequestParam(required = false) Long layiheId,
                          @RequestParam(required = false) Long kassaHesabiId,
                          @RequestParam(required = false) String hesablasmaHesabi,
                          @RequestParam(required = false) Long kontragentId,
                          @RequestParam String tesvir,
                          @RequestParam(required = false) BigDecimal medaxil,
                          @RequestParam(required = false) BigDecimal mexaric,
                          @RequestParam(required = false) String xercMaddesi,
                          RedirectAttributes redirectAttributes) {
        try {
            KassaEmeliyyati yeni = new KassaEmeliyyati();
            yeni.setTarix(LocalDate.parse(tarix));
            if (layiheId != null) yeni.setLayihe(layiheRepo.findById(layiheId).orElse(null));
            if (kassaHesabiId != null) yeni.setKassaHesabi(kassaHesabiRepo.findById(kassaHesabiId).orElse(null));
            yeni.setHesablasmaHesabi(hesablasmaHesabi);
            if (kontragentId != null) {
                yeni.setKontragent(kontragentService.hamisiniGetir().stream()
                        .filter(k -> k.getId().equals(kontragentId)).findFirst().orElse(null));
            }
            yeni.setTesvir(tesvir);
            yeni.setMedaxil(medaxil != null ? medaxil : BigDecimal.ZERO);
            yeni.setMexaric(mexaric != null ? mexaric : BigDecimal.ZERO);
            yeni.setXercMaddesi(xercMaddesi);
            service.elaveEt(yeni);
            redirectAttributes.addFlashAttribute("ugur", "Kassa əməliyyatı uğurla yaradıldı!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/kassa";
    }

    // YENİ: Redaktə səhifəsi
    @GetMapping("/redakte/{id}")
    public String redakteSehife(@PathVariable Long id, Model model,
                                RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Istifadeci user = istifadeciRepo.findByUsername(username);
        boolean isAdmin = user != null && user.getRol().equals("ROLE_ADMIN");

        KassaEmeliyyati e = service.findById(id);

        // YENİ: Əgər təsdiqlənibsə və istifadəçi admin deyilsə, redaktə etməyə icazə vermə
        if (e.getTesdiqlenib() != null && e.getTesdiqlenib() && !isAdmin) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Təsdiqlənmiş əməliyyatı yalnız ADMIN redaktə edə bilər!");
            return "redirect:/kassa";
        }

        model.addAttribute("emeliyyat", e);
        model.addAttribute("kassaHesablari", kassaHesabiRepo.findAll());
        model.addAttribute("kontragentler", kontragentService.hamisiniGetir());
        model.addAttribute("layiheler", layiheRepo.findAll());
        return "kassa-redakte";
    }

    // YENİ: Redaktəni yadda saxla
    @PostMapping("/redakte/{id}")
    public String redakteEt(@PathVariable Long id,
                            @RequestParam String tarix,
                            @RequestParam(required = false) Long layiheId,
                            @RequestParam(required = false) Long kassaHesabiId,
                            @RequestParam(required = false) String hesablasmaHesabi,
                            @RequestParam(required = false) Long kontragentId,
                            @RequestParam String tesvir,
                            @RequestParam(required = false) BigDecimal medaxil,
                            @RequestParam(required = false) BigDecimal mexaric,
                            @RequestParam(required = false) String xercMaddesi,
                            RedirectAttributes redirectAttributes) {
        try {
            KassaEmeliyyati yeni = new KassaEmeliyyati();
            yeni.setTarix(LocalDate.parse(tarix));
            if (layiheId != null) yeni.setLayihe(layiheRepo.findById(layiheId).orElse(null));
            if (kassaHesabiId != null) yeni.setKassaHesabi(kassaHesabiRepo.findById(kassaHesabiId).orElse(null));
            yeni.setHesablasmaHesabi(hesablasmaHesabi);
            if (kontragentId != null) {
                yeni.setKontragent(kontragentService.hamisiniGetir().stream()
                        .filter(k -> k.getId().equals(kontragentId)).findFirst().orElse(null));
            }
            yeni.setTesvir(tesvir);
            yeni.setMedaxil(medaxil);
            yeni.setMexaric(mexaric);
            yeni.setXercMaddesi(xercMaddesi);
            service.redakteEt(id, yeni);
            redirectAttributes.addFlashAttribute("ugur", "Əməliyyat uğurla redaktə edildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/kassa";
    }

    @PostMapping("/sil/{id}")
    public String sil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Istifadeci user = istifadeciRepo.findByUsername(username);
        if (user == null || !user.getRol().equals("ROLE_ADMIN")) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Yalnız ADMIN silə bilər!");
            return "redirect:/kassa";
        }
        try {
            service.sil(id);
            redirectAttributes.addFlashAttribute("ugur", "Əməliyyat silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/kassa";
    }

    @PostMapping("/tesdiqle/{id}")
    public String tesdiqle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Istifadeci user = istifadeciRepo.findByUsername(username);
        if (user == null || !user.getRol().equals("ROLE_ADMIN")) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Yalnız ADMIN təsdiqləyə bilər!");
            return "redirect:/kassa";
        }
        try {
            service.tesdiqle(id);
            redirectAttributes.addFlashAttribute("ugur", "Əməliyyat təsdiqləndi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/kassa";
    }
}