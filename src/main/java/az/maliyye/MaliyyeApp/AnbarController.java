package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/anbar")
public class AnbarController {

    @Autowired private AnbarService service;
    @Autowired private AnbarRepository anbarRepo;
    @Autowired private MehsulRepository mehsulRepo;
    @Autowired private OlcuVahidiRepository olcuVahidiRepo;
    @Autowired private MalTipiRepository malTipiRepo;
    @Autowired private MehsulQrupuRepository mehsulQrupuRepo;
    @Autowired private KontragentRepository kontragentRepo;
    @Autowired private IstifadeciRepository istifadeciRepo;
    @Autowired private AnbarMehsulRepository anbarMehsulRepository;

    @GetMapping
    public String anbarMenyu() {
        return "anbar";
    }

    @GetMapping("/api/qaliqlar")
    @ResponseBody
    public Map<Long, Double> getAnbarQaliqlari(@RequestParam Long anbarId) {
        List<AnbarMehsul> qaliqlar = anbarMehsulRepository.findByAnbar_Id(anbarId);
        return qaliqlar.stream()
                .collect(Collectors.toMap(
                        am -> am.getMehsul().getId(),
                        am -> am.getMiqdar() != null ? am.getMiqdar() : 0.0,
                        (existing, replacement) -> existing
                ));
    }

    @GetMapping({"/alis", "/mal-xidmet-alisi", "/alis-qaimesi"})
    public String alisSehife(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("kontragentler", kontragentRepo.findAll());
        model.addAttribute("anbarlar", service.icazeliAnbarlar(username));
        model.addAttribute("mehsullar", mehsulRepo.findAll());
        return "alis-qaimesi";
    }

    @GetMapping("/mallar")
    public String mallarSehife(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Istifadeci user = istifadeciRepo.findByUsername(username);

        boolean isAdmin = (user != null && "ROLE_ADMIN".equals(user.getRol()));

        model.addAttribute("qruplar", mehsulQrupuRepo.findAll());
        model.addAttribute("malTipleri", malTipiRepo.findAll());
        model.addAttribute("mehsullar", service.butunMehsullar());
        model.addAttribute("anbarlar", service.icazeliAnbarlar(username));
        model.addAttribute("olcuVahidleri", olcuVahidiRepo.findAll());
        model.addAttribute("isAdmin", isAdmin);
        return "mallar";
    }

    @GetMapping("/anbar-qaliqlari")
    public String anbarQaliqlariSehife(@RequestParam(required = false) Long anbarId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Istifadeci user = istifadeciRepo.findByUsername(username);

        // İstifadəçi icazələrinə uyğun anbar qalıqları sətrləri çəkilir
        List<AnbarMehsul> butunQaliqlar = anbarMehsulRepository.findAll();

        if (user != null && !user.getRol().equals("ROLE_ADMIN")) {
            List<Anbar> icazeliAnbarlar = user.getAllowedWarehouses();
            butunQaliqlar = butunQaliqlar.stream()
                    .filter(am -> icazeliAnbarlar.contains(am.getAnbar()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("anbarMehsullar", butunQaliqlar);
        model.addAttribute("anbarlar", service.icazeliAnbarlar(username));
        model.addAttribute("mehsullar", mehsulRepo.findAll());
        model.addAttribute("qruplar", mehsulQrupuRepo.findAll());
        model.addAttribute("malTipleri", malTipiRepo.findAll());
        model.addAttribute("secilmisAnbarId", anbarId);
        return "anbar-qaliqlari";
    }

    @GetMapping("/olcu-vahidi")
    public String olcuVahidiSehife(Model model) {
        model.addAttribute("olcuVahidleri", olcuVahidiRepo.findAll());
        return "olcu-vahidi";
    }

    @PostMapping("/olcu-vahidi-elave-et")
    public String olcuVahidiElaveEt(@RequestParam String ad, @RequestParam String qisaAd, RedirectAttributes redirectAttributes) {
        try {
            olcuVahidiRepo.save(new OlcuVahidi(ad, qisaAd));
            redirectAttributes.addFlashAttribute("ugur", "Ölçü vahidi əlavə edildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/olcu-vahidi";
    }

    @GetMapping("/olcu-vahidi-redakte/{id}")
    public String olcuVahidiRedakteSehife(@PathVariable Long id, Model model) {
        OlcuVahidi ov = olcuVahidiRepo.findById(id).orElseThrow();
        model.addAttribute("olcuVahidi", ov);
        return "olcu-vahidi-redakte";
    }

    @PostMapping("/olcu-vahidi-redakte/{id}")
    public String olcuVahidiRedakteEt(@PathVariable Long id, @RequestParam String ad, @RequestParam String qisaAd, RedirectAttributes redirectAttributes) {
        try {
            OlcuVahidi ov = olcuVahidiRepo.findById(id).orElseThrow();
            ov.setAd(ad);
            ov.setQisaAd(qisaAd);
            olcuVahidiRepo.save(ov);
            redirectAttributes.addFlashAttribute("ugur", "Ölçü vahidi yeniləndi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/olcu-vahidi";
    }

    @PostMapping("/olcu-vahidi-sil/{id}")
    public String olcuVahidiSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            olcuVahidiRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("ugur", "Ölçü vahidi silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Silinə bilmədi.");
        }
        return "redirect:/anbar/olcu-vahidi";
    }

    @GetMapping({"/mal-tipi", "/malin-tipi"})
    public String malTipiSehife(Model model) {
        model.addAttribute("malTipleri", malTipiRepo.findAll());
        return "mal-tipi";
    }

    @PostMapping("/mal-tipi-elave-et")
    public String malTipiElaveEt(@RequestParam String ad, RedirectAttributes redirectAttributes) {
        try {
            malTipiRepo.save(new MalTipi(ad));
            redirectAttributes.addFlashAttribute("ugur", "Mal tipi əlavə edildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/mal-tipi";
    }

    @GetMapping("/mal-tipi-redakte/{id}")
    public String malTipiRedakteSehife(@PathVariable Long id, Model model) {
        MalTipi malTipi = malTipiRepo.findById(id).orElseThrow();
        model.addAttribute("malTipi", malTipi);
        return "mal-tipi-redakte";
    }

    @PostMapping("/mal-tipi-redakte/{id}")
    public String malTipiRedakteEt(@PathVariable Long id, @RequestParam String ad, RedirectAttributes redirectAttributes) {
        try {
            MalTipi malTipi = malTipiRepo.findById(id).orElseThrow();
            malTipi.setAd(ad);
            malTipiRepo.save(malTipi);
            redirectAttributes.addFlashAttribute("ugur", "Mal tipi yeniləndi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/mal-tipi";
    }

    @PostMapping("/mal-tipi-sil/{id}")
    public String malTipiSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            malTipiRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("ugur", "Mal tipi silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Silinə bilmədi.");
        }
        return "redirect:/anbar/mal-tipi";
    }

    @GetMapping({"/mal-qrupu", "/malin-qrupu"})
    public String malQrupuSehife(Model model) {
        model.addAttribute("qruplar", mehsulQrupuRepo.findAll());
        return "mal-qrupu";
    }

    @PostMapping("/qrup-elave-et")
    public String qrupElaveEt(@RequestParam String qrupAdi, RedirectAttributes redirectAttributes) {
        if(qrupAdi != null && !qrupAdi.trim().isEmpty()) {
            service.qrupElaveEt(qrupAdi.trim());
            redirectAttributes.addFlashAttribute("ugur", "Mal qrupu əlavə edildi!");
        }
        return "redirect:/anbar/mal-qrupu";
    }

    @GetMapping("/mal-qrupu-redakte/{id}")
    public String malQrupuRedakteSehife(@PathVariable Long id, Model model) {
        MehsulQrupu qrup = mehsulQrupuRepo.findById(id).orElseThrow();
        model.addAttribute("qrup", qrup);
        return "mal-qrupu-redakte";
    }

    @PostMapping("/mal-qrupu-redakte/{id}")
    public String malQrupuRedakteEt(@PathVariable Long id, @RequestParam String qrupAdi, RedirectAttributes redirectAttributes) {
        try {
            MehsulQrupu qrup = mehsulQrupuRepo.findById(id).orElseThrow();
            qrup.setAd(qrupAdi);
            mehsulQrupuRepo.save(qrup);
            redirectAttributes.addFlashAttribute("ugur", "Mal qrupu yeniləndi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/mal-qrupu";
    }

    @PostMapping("/qrup-sil/{id}")
    public String qrupSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            long mehsulSayi = service.qrupdaMehsulSayi(id);
            if (mehsulSayi > 0) {
                redirectAttributes.addFlashAttribute("xeta", "XƏTA: Bu qrupda " + mehsulSayi + " məhsul var! Əvvəlcə məhsulları silin.");
                return "redirect:/anbar/mal-qrupu";
            }
            service.qrupSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Məhsul qrupu silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/mal-qrupu";
    }

    @PostMapping("/mehsul-elave-et")
    public String mehsulElaveEt(@RequestParam String mehsulAdi,
                                @RequestParam Long qrupId,
                                @RequestParam(required = false) Long malTipiId,
                                @RequestParam String vahid,
                                @RequestParam String alisQiymeti,
                                @RequestParam String satisQiymeti,
                                @RequestParam(required = false) Long anbarId,
                                @RequestParam(required = false) String miqdar,
                                RedirectAttributes redirectAttributes) {
        try {
            BigDecimal alisQiymetiDec = new BigDecimal(alisQiymeti);
            BigDecimal satisQiymetiDec = new BigDecimal(satisQiymeti);
            Double miqdarDouble = (miqdar != null && !miqdar.isEmpty()) ? Double.parseDouble(miqdar) : 0.0;

            service.mehsulElaveEt(mehsulAdi, qrupId, malTipiId, vahid, alisQiymetiDec, satisQiymetiDec, anbarId, miqdarDouble);
            redirectAttributes.addFlashAttribute("ugur", "Məhsul uğurla əlavə edildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Məhsul əlavə olunmadı. Səbəb: " + e.getMessage());
        }
        return "redirect:/anbar/mallar";
    }

    @GetMapping("/mehsul-redakte/{id}")
    public String mehsulRedakteSehife(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Istifadeci user = istifadeciRepo.findByUsername(username);

        if (user == null || !"ROLE_ADMIN".equals(user.getRol())) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Məhsulu redaktə etmək üçün yalnız Admin səlahiyyəti tələb olunur!");
            return "redirect:/anbar/mallar";
        }

        Mehsul mehsul = mehsulRepo.findById(id).orElseThrow();

        Double cariMiqdar = mehsul.getMiqdar() != null ? mehsul.getMiqdar() : 0.0;
        List<AnbarMehsul> qaliqlar = service.anbarQaliqlari(null).stream()
                .filter(am -> am.getMehsul().getId().equals(id)).toList();
        if (!qaliqlar.isEmpty()) {
            cariMiqdar = qaliqlar.stream().mapToDouble(AnbarMehsul::getMiqdar).sum();
        }

        model.addAttribute("mehsul", mehsul);
        model.addAttribute("cariMiqdar", cariMiqdar);
        model.addAttribute("qruplar", mehsulQrupuRepo.findAll());
        model.addAttribute("malTipleri", malTipiRepo.findAll());
        model.addAttribute("anbarlar", service.icazeliAnbarlar(username));
        model.addAttribute("olcuVahidleri", olcuVahidiRepo.findAll());
        return "mehsul-redakte";
    }

    @PostMapping("/mehsul-redakte/{id}")
    public String mehsulRedakteEt(@PathVariable Long id,
                                  @RequestParam String mehsulAdi,
                                  @RequestParam Long qrupId,
                                  @RequestParam(required = false) Long malTipiId,
                                  @RequestParam String vahid,
                                  @RequestParam String alisQiymeti,
                                  @RequestParam String satisQiymeti,
                                  @RequestParam(required = false) Long anbarId,
                                  @RequestParam(required = false) String miqdar,
                                  RedirectAttributes redirectAttributes) {
        try {
            BigDecimal alisQiymetiDec = new BigDecimal(alisQiymeti);
            BigDecimal satisQiymetiDec = new BigDecimal(satisQiymeti);
            Double miqdarDouble = (miqdar != null && !miqdar.isEmpty()) ? Double.parseDouble(miqdar) : 0.0;

            service.mehsulRedakteEt(id, mehsulAdi, qrupId, malTipiId, vahid, alisQiymetiDec, satisQiymetiDec, anbarId, miqdarDouble);
            redirectAttributes.addFlashAttribute("ugur", "Məhsul uğurla redaktə edildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Məhsul redaktə olunmadı. Səbəb: " + e.getMessage());
        }
        return "redirect:/anbar/mallar";
    }

    @PostMapping("/mehsul-sil/{id}")
    public String mehsulSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.mehsulSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Məhsul uğurla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Məhsul silinə bilmədi. Səbəb: " + e.getMessage());
        }
        return "redirect:/anbar/mallar";
    }

    @GetMapping("/esas-vesaitler") public String esasVesaitlerSehife() { return "esas-vesaitler"; }
    @GetMapping("/qeyri-maddi") public String qeyriMaddiSehife() { return "qeyri-maddi"; }

    @GetMapping("/{sehife}")
    public String bosSehife(@PathVariable String sehife, Model model) {
        model.addAttribute("sehifeAdi", sehife);
        return "tezlikle";
    }
}