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
@RequestMapping("/hr")
public class HrController {

    @Autowired private IsciRepository isciRepo;
    @Autowired private SobeRepository sobeRepo;
    @Autowired private VezifeRepository vezifeRepo;
    @Autowired private DavamiyyetRepository davamiyyetRepo;
    @Autowired private MezuniyyetRepository mezuniyyetRepo;
    @Autowired private IstifadeciRepository istifadeciRepo;
    @Autowired private BayramService bayramService;

    // ============================================
    // === DASHBOARD ===
    // ============================================
    @GetMapping
    public String hrDashboard(Model model) {
        model.addAttribute("isciSayi", isciRepo.count());
        model.addAttribute("aktivIsciSayi", isciRepo.countByStatus("AKTIV"));
        model.addAttribute("sobeSayi", sobeRepo.count());
        model.addAttribute("vezifeSayi", vezifeRepo.count());
        return "hr/dashboard";
    }

    // ============================================
    // === İŞÇİLƏR ===
    // ============================================
    @GetMapping("/isciler")
    public String isciler(Model model) {
        model.addAttribute("isciler", isciRepo.findAll());
        model.addAttribute("sobeler", sobeRepo.findAll());
        model.addAttribute("vezifeler", vezifeRepo.findAll());
        return "hr/isciler";
    }

    @PostMapping("/isciler/elave-et")
    public String isciElaveEt(@RequestParam String ad,
                              @RequestParam String soyad,
                              @RequestParam(required = false) String ataAdi,
                              @RequestParam(required = false) String finKod,
                              @RequestParam(required = false) String telefon,
                              @RequestParam(required = false) String email,
                              @RequestParam(required = false) Long sobeId,
                              @RequestParam(required = false) Long vezifeId,
                              @RequestParam(required = false) BigDecimal emekHaqqi,
                              RedirectAttributes ra) {
        try {
            Isci i = new Isci();
            i.setAd(ad);
            i.setSoyad(soyad);
            i.setAtaAdi(ataAdi);
            i.setFinKod(finKod);
            i.setTelefon(telefon);
            i.setEmail(email);
            if (sobeId != null) i.setSobe(sobeRepo.findById(sobeId).orElse(null));
            if (vezifeId != null) i.setVezife(vezifeRepo.findById(vezifeId).orElse(null));
            i.setEmekHaqqi(emekHaqqi != null ? emekHaqqi : BigDecimal.ZERO);
            i.setIseQebulTarixi(LocalDate.now());
            i.setStatus("AKTIV");

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                i.setYaradanIstifadeci(istifadeciRepo.findByUsername(auth.getName()));
            }

            isciRepo.save(i);
            ra.addFlashAttribute("ugur", "İşçi uğurla əlavə edildi!");
        } catch (Exception e) {
            ra.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/hr/isciler";
    }

    @PostMapping("/isciler/sil/{id}")
    public String isciSil(@PathVariable Long id, RedirectAttributes ra) {
        try {
            isciRepo.deleteById(id);
            ra.addFlashAttribute("ugur", "İşçi silindi.");
        } catch (Exception e) {
            ra.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/hr/isciler";
    }

    // ============================================
    // === ŞÖBƏLƏR ===
    // ============================================
    @GetMapping("/sobeler")
    public String sobeler(Model model) {
        model.addAttribute("sobeler", sobeRepo.findAll());
        model.addAttribute("isciler", isciRepo.findAll());
        return "hr/sobeler";
    }

    @PostMapping("/sobeler/elave-et")
    public String sobeElaveEt(@RequestParam String ad,
                              @RequestParam(required = false) String tesvir,
                              @RequestParam(required = false) Long rehberId,
                              RedirectAttributes ra) {
        try {
            Sobe s = new Sobe(ad);
            s.setTesvir(tesvir);
            if (rehberId != null) s.setRehber(isciRepo.findById(rehberId).orElse(null));
            sobeRepo.save(s);
            ra.addFlashAttribute("ugur", "Şöbə əlavə edildi!");
        } catch (Exception e) {
            ra.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/hr/sobeler";
    }

    @PostMapping("/sobeler/sil/{id}")
    public String sobeSil(@PathVariable Long id, RedirectAttributes ra) {
        try {
            sobeRepo.deleteById(id);
            ra.addFlashAttribute("ugur", "Şöbə silindi.");
        } catch (Exception e) {
            ra.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/hr/sobeler";
    }

    // ============================================
    // === VƏZİFƏLƏR ===
    // ============================================
    @GetMapping("/vezifeler")
    public String vezifeler(Model model) {
        model.addAttribute("vezifeler", vezifeRepo.findAll());
        return "hr/vezifeler";
    }

    @PostMapping("/vezifeler/elave-et")
    public String vezifeElaveEt(@RequestParam String ad,
                                @RequestParam(required = false) String tesvir,
                                @RequestParam(required = false) BigDecimal esasEmekHaqqi,
                                RedirectAttributes ra) {
        try {
            Vezife v = new Vezife(ad);
            v.setTesvir(tesvir);
            v.setEsasEmekHaqqi(esasEmekHaqqi != null ? esasEmekHaqqi : BigDecimal.ZERO);
            vezifeRepo.save(v);
            ra.addFlashAttribute("ugur", "Vəzifə əlavə edildi!");
        } catch (Exception e) {
            ra.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/hr/vezifeler";
    }

    @PostMapping("/vezifeler/sil/{id}")
    public String vezifeSil(@PathVariable Long id, RedirectAttributes ra) {
        try {
            vezifeRepo.deleteById(id);
            ra.addFlashAttribute("ugur", "Vəzifə silindi.");
        } catch (Exception e) {
            ra.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/hr/vezifeler";
    }

    // ============================================
    // === TABEL / DAVAMİYYƏT ===
    // ============================================
    @GetMapping("/tabel")
    public String tabel(@RequestParam(required = false) Integer il,
                        @RequestParam(required = false) Integer ay,
                        Model model) {
        int cariIl = LocalDate.now().getYear();
        int cariAy = LocalDate.now().getMonthValue();

        int secilmisIl = (il != null) ? il : cariIl;
        int secilmisAy = (ay != null) ? ay : cariAy;

        LocalDate ayBaslangic = LocalDate.of(secilmisIl, secilmisAy, 1);
        LocalDate aySon = ayBaslangic.withDayOfMonth(ayBaslangic.lengthOfMonth());

        List<Isci> isciler = isciRepo.findAll();
        List<Davamiyyet> davamiyyetler = davamiyyetRepo.findByTarixBetween(ayBaslangic, aySon);

        // Debug: işçi sayını yoxla
        System.out.println(">>> TABEL: Tapılan işçi sayı = " + isciler.size());
        for (Isci i : isciler) {
            System.out.println(">>>   İşçi: " + i.getTamAd() + " | Status: " + i.getStatus());
        }

        model.addAttribute("isciler", isciler);
        model.addAttribute("davamiyyetler", davamiyyetler);
        model.addAttribute("ayBaslangic", ayBaslangic);
        model.addAttribute("aySon", aySon);
        model.addAttribute("secilmisIl", secilmisIl);
        model.addAttribute("secilmisAy", secilmisAy);
        model.addAttribute("cariIl", cariIl);
        model.addAttribute("gunSayi", ayBaslangic.lengthOfMonth());
        model.addAttribute("bayramService", bayramService);

        return "hr/tabel";
    }

    @PostMapping("/tabel/elave-et")
    public String davamiyyetElaveEt(@RequestParam Long isciId,
                                    @RequestParam String tarix,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) String qeyd,
                                    RedirectAttributes ra) {
        try {
            LocalDate tarixDate = LocalDate.parse(tarix);
            Davamiyyet d = davamiyyetRepo.findByIsci_IdAndTarix(isciId, tarixDate);
            if (d == null) d = new Davamiyyet();

            d.setIsci(isciRepo.findById(isciId).orElseThrow());
            d.setTarix(tarixDate);
            d.setStatus(status != null ? status : "IS");
            d.setQeyd(qeyd);
            davamiyyetRepo.save(d);
            ra.addFlashAttribute("ugur", "Davamiyyət qeyd edildi!");
        } catch (Exception e) {
            ra.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/hr/tabel";
    }

    @PostMapping("/tabel/toplu-qeyd")
    public String topluQeyd(@RequestParam Long isciId,
                            @RequestParam String ayBaslangic,
                            @RequestParam String status,
                            RedirectAttributes ra) {
        try {
            LocalDate baslangic = LocalDate.parse(ayBaslangic);
            LocalDate son = baslangic.withDayOfMonth(baslangic.lengthOfMonth());

            for (LocalDate d = baslangic; !d.isAfter(son); d = d.plusDays(1)) {
                if (d.getDayOfWeek().getValue() >= 6) continue;
                if (bayramService.isBayram(d)) continue;

                Davamiyyet dav = davamiyyetRepo.findByIsci_IdAndTarix(isciId, d);
                if (dav == null) dav = new Davamiyyet();
                dav.setIsci(isciRepo.findById(isciId).orElseThrow());
                dav.setTarix(d);
                dav.setStatus(status);
                davamiyyetRepo.save(dav);
            }
            ra.addFlashAttribute("ugur", "Toplu qeyd uğurla tamamlandı!");
        } catch (Exception e) {
            ra.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/hr/tabel";
    }

    // YENİ: Bütün işçilər üçün toplu qeyd
    @PostMapping("/tabel/hamisi-toplu-qeyd")
    public String hamisiTopluQeyd(@RequestParam String ayBaslangic,
                                  @RequestParam String status,
                                  RedirectAttributes ra) {
        try {
            LocalDate baslangic = LocalDate.parse(ayBaslangic);
            LocalDate son = baslangic.withDayOfMonth(baslangic.lengthOfMonth());
            List<Isci> isciler = isciRepo.findAll();

            int qeydSayi = 0;
            for (Isci isci : isciler) {
                for (LocalDate d = baslangic; !d.isAfter(son); d = d.plusDays(1)) {
                    if (d.getDayOfWeek().getValue() >= 6) continue;
                    if (bayramService.isBayram(d)) continue;

                    Davamiyyet dav = davamiyyetRepo.findByIsci_IdAndTarix(isci.getId(), d);
                    if (dav == null) dav = new Davamiyyet();
                    dav.setIsci(isci);
                    dav.setTarix(d);
                    dav.setStatus(status);
                    davamiyyetRepo.save(dav);
                    qeydSayi++;
                }
            }
            ra.addFlashAttribute("ugur", "Bütün işçilər üçün " + qeydSayi + " qeyd yaradıldı!");
        } catch (Exception e) {
            ra.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/hr/tabel";
    }

    // ============================================
    // === MƏZUNİYYƏTLƏR ===
    // ============================================
    @GetMapping("/mezuniyyetler")
    public String mezuniyyetler(Model model) {
        model.addAttribute("mezuniyyetler", mezuniyyetRepo.findAll());
        model.addAttribute("isciler", isciRepo.findAll());
        return "hr/mezuniyyetler";
    }

    @PostMapping("/mezuniyyetler/elave-et")
    public String mezuniyyetElaveEt(@RequestParam Long isciId,
                                    @RequestParam String nov,
                                    @RequestParam String baslamaTarixi,
                                    @RequestParam String bitmeTarixi,
                                    @RequestParam(required = false) String sebeb,
                                    RedirectAttributes ra) {
        try {
            Mezuniyyet m = new Mezuniyyet();
            m.setIsci(isciRepo.findById(isciId).orElseThrow());
            m.setNov(nov);
            m.setBaslamaTarixi(LocalDate.parse(baslamaTarixi));
            m.setBitmeTarixi(LocalDate.parse(bitmeTarixi));
            m.setSebeb(sebeb);
            m.setStatus("GOZLEYIR");
            mezuniyyetRepo.save(m);
            ra.addFlashAttribute("ugur", "Məzuniyyət sorğusu yaradıldı!");
        } catch (Exception e) {
            ra.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/hr/mezuniyyetler";
    }

    // ============================================
    // === ƏMƏK HAQQI ===
    // ============================================
    @GetMapping("/emek-haqqi")
    public String emekHaqqi(Model model) {
        model.addAttribute("isciler", isciRepo.findAll());
        return "hr/emek-haqqi";
    }

    // ============================================
    // === HESABATLAR ===
    // ============================================
    @GetMapping("/hesabatlar")
    public String hesabatlar(Model model) {
        model.addAttribute("isciSayi", isciRepo.count());
        model.addAttribute("sobeSayi", sobeRepo.count());
        return "hr/hesabatlar";
    }
}