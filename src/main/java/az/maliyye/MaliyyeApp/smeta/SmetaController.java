package az.maliyye.MaliyyeApp.smeta;

import az.maliyye.MaliyyeApp.layiheler.LayiheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/smetalar")
public class SmetaController {

    @Autowired private SmetaService smetaService;
    @Autowired private LayiheRepository layiheRepository;
    @Autowired private FaktikiXercRepository faktikiXercRepository;

    @GetMapping
    public String smetalarSiyahisi(Model model) {
        try {
            model.addAttribute("smetalar", smetaService.butunSmetalar());
            model.addAttribute("layiheler", layiheRepository.findAll());
            return "smeta/smetalar";
        } catch (Exception e) {
            model.addAttribute("xeta", "Siyahı yüklənərkən xəta: " + e.getMessage());
            return "smeta/smetalar";
        }
    }

    @PostMapping("/elave-et")
    public String smetaElaveEt(@RequestParam Long layiheId, @RequestParam String ad, @RequestParam String versiya, @RequestParam String valyuta, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String istifadeci = principal != null ? principal.getName() : "Admin";
            smetaService.smetaYarat(layiheId, ad, versiya, valyuta, istifadeci);
            redirectAttributes.addFlashAttribute("ugur", "Smeta uğurla yaradıldı!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/smetalar";
    }

    @GetMapping("/detal/{id}")
    public String smetaDetal(@PathVariable Long id,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tarix,
                             Model model) {
        try {
            LocalDateTime tarixZamani = (tarix != null) ? tarix.atTime(23, 59, 59) : null;
            Smeta smeta = smetaService.smetaTap(id, tarixZamani);
            List<SmetaBendi> bəndlər = smeta.getBəndlər();
            double umumiFaktikiXerc = 0.0;
            if (bəndlər != null) {
                for (SmetaBendi bend : bəndlər) {
                    umumiFaktikiXerc += bend.getFaktikiXerc();
                }
            }
            model.addAttribute("smeta", smeta);
            model.addAttribute("bəndlər", bəndlər);
            model.addAttribute("umumiFaktikiXerc", umumiFaktikiXerc);
            model.addAttribute("secilmisTarix", tarix);
            return "smeta/smeta-detal";
        } catch (Exception e) {
            return "redirect:/smetalar";
        }
    }

    // --- BAŞLIQ ---
    @PostMapping("/detal/{smetaId}/basliq-elave-et")
    public String basliqElaveEt(@PathVariable Long smetaId, @RequestParam String kod, @RequestParam String ad, RedirectAttributes redirectAttributes) {
        try {
            smetaService.basliqElaveEt(smetaId, kod, ad);
            redirectAttributes.addFlashAttribute("ugur", "Yeni bölmə başlığı əlavə edildi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/smetalar/detal/" + smetaId;
    }

    @PostMapping("/detal/basliq-redakte/{basliqId}")
    @ResponseBody
    public String basliqRedakteEt(@PathVariable Long basliqId, @RequestParam String kod, @RequestParam String ad) {
        try {
            smetaService.basliqRedakteEt(basliqId, kod, ad);
            return "OK";
        } catch (Exception e) {
            return "XƏTA: " + e.getMessage();
        }
    }

    @PostMapping("/detal/{smetaId}/basliq-sil/{basliqId}")
    public String basliqSil(@PathVariable Long smetaId, @PathVariable Long basliqId, RedirectAttributes redirectAttributes) {
        try {
            smetaService.basliqSil(basliqId);
            redirectAttributes.addFlashAttribute("ugur", "Bölmə başlığı silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/smetalar/detal/" + smetaId;
    }

    // --- ALT BAŞLIQ ---
    @PostMapping("/detal/{smetaId}/alt-basliq-elave-et")
    public String altBasliqElaveEt(@PathVariable Long smetaId, @RequestParam Long basliqId, @RequestParam String kod, @RequestParam String ad, RedirectAttributes redirectAttributes) {
        try {
            smetaService.altBasliqElaveEt(basliqId, kod, ad);
            redirectAttributes.addFlashAttribute("ugur", "Alt başlıq əlavə edildi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/smetalar/detal/" + smetaId;
    }

    @PostMapping("/detal/alt-basliq-redakte/{altBasliqId}")
    @ResponseBody
    public String altBasliqRedakteEt(@PathVariable Long altBasliqId, @RequestParam String kod, @RequestParam String ad) {
        try {
            smetaService.altBasliqRedakteEt(altBasliqId, kod, ad);
            return "OK";
        } catch (Exception e) {
            return "XƏTA: " + e.getMessage();
        }
    }

    @PostMapping("/detal/{smetaId}/alt-basliq-sil/{altBasliqId}")
    public String altBasliqSil(@PathVariable Long smetaId, @PathVariable Long altBasliqId, RedirectAttributes redirectAttributes) {
        try {
            smetaService.altBasliqSil(altBasliqId);
            redirectAttributes.addFlashAttribute("ugur", "Alt başlıq silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/smetalar/detal/" + smetaId;
    }

    // --- BƏND ---
    @PostMapping("/detal/{smetaId}/bend-elave-et")
    public String bendElaveEt(@PathVariable Long smetaId,
                              @RequestParam(required = false) Long altBasliqId,
                              @RequestParam String kod,
                              @RequestParam String isAdi,
                              @RequestParam String vahid,
                              @RequestParam Double miqdar,
                              @RequestParam Double vahidQiymeti,
                              @RequestParam String kateqoriya,
                              RedirectAttributes redirectAttributes) {
        try {
            smetaService.bendElaveEt(smetaId, altBasliqId, kod, isAdi, vahid, miqdar, vahidQiymeti, kateqoriya);
            redirectAttributes.addFlashAttribute("ugur", "Smeta bəndi (iş) əlavə olundu.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/smetalar/detal/" + smetaId;
    }

    @PostMapping("/detal/bend-sil/{smetaId}/{bendiId}")
    public String bendSil(@PathVariable Long smetaId, @PathVariable Long bendiId, RedirectAttributes redirectAttributes) {
        try {
            smetaService.bendSil(bendiId);
            redirectAttributes.addFlashAttribute("ugur", "Smeta bəndi silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/smetalar/detal/" + smetaId;
    }

    @GetMapping("/detal/bend-redakte/{smetaId}/{bendiId}")
    public String bendRedakteSehife(@PathVariable Long smetaId, @PathVariable Long bendiId, Model model) {
        try {
            Smeta smeta = smetaService.smetaTap(smetaId);
            SmetaBendi bendi = smetaService.bendTap(bendiId);
            model.addAttribute("smeta", smeta);
            model.addAttribute("bendi", bendi);
            return "smeta/bend-redakte";
        } catch (Exception e) {
            return "redirect:/smetalar/detal/" + smetaId;
        }
    }

    @PostMapping("/detal/bend-redakte/{smetaId}/{bendiId}")
    public String bendRedakteEt(@PathVariable Long smetaId,
                                @PathVariable Long bendiId,
                                @RequestParam String kod,
                                @RequestParam String isAdi,
                                @RequestParam String vahid,
                                @RequestParam Double miqdar,
                                @RequestParam Double vahidQiymeti,
                                @RequestParam String kateqoriya,
                                RedirectAttributes redirectAttributes) {
        try {
            smetaService.bendRedakteEt(bendiId, kod, isAdi, vahid, miqdar, vahidQiymeti, kateqoriya);
            redirectAttributes.addFlashAttribute("ugur", "Bənd uğurla redaktə edildi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/smetalar/detal/" + smetaId;
    }

    // --- TƏSDİQLƏMƏ ---
    @PostMapping("/tesdiqle/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String budceyeCevirVeTesdiqle(@PathVariable Long id, @RequestParam(required = false) Double gozlenilmezFaiz, @RequestParam(required = false) Double dolayiFaiz, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String istifadeci = principal != null ? principal.getName() : "Admin";
            smetaService.budceyeCevirVeTesdiqle(id, gozlenilmezFaiz, dolayiFaiz, istifadeci);
            redirectAttributes.addFlashAttribute("ugur", "Smeta təsdiqləndi və Büdcəyə çevrildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/smetalar/detal/" + id;
    }

    @PostMapping("/sil/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String smetaSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            smetaService.smetaSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Smeta silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/smetalar";
    }

    @GetMapping("/redakte/{id}")
    public String smetaRedakteSehife(@PathVariable Long id, Model model) {
        try {
            Smeta smeta = smetaService.smetaTap(id);
            model.addAttribute("smeta", smeta);
            model.addAttribute("layiheler", layiheRepository.findAll());
            return "smeta/smeta-redakte";
        } catch (Exception e) {
            return "redirect:/smetalar";
        }
    }

    @PostMapping("/redakte/{id}")
    public String smetaRedakteEt(@PathVariable Long id, @RequestParam Long layiheId, @RequestParam String ad, @RequestParam String versiya, @RequestParam String valyuta, RedirectAttributes redirectAttributes) {
        try {
            smetaService.smetaRedakteEt(id, layiheId, ad, versiya, valyuta);
            redirectAttributes.addFlashAttribute("ugur", "Smeta uğurla yeniləndi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/smetalar";
    }

    @GetMapping("/excel/{id}")
    public void excelIxracEt(@PathVariable Long id, jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        try {
            Smeta smeta = smetaService.smetaTap(id);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"Smeta_" + smeta.getAd() + ".xlsx\"");
            smetaService.excelIxracEt(id, response.getOutputStream());
        } catch (Exception e) {
            response.sendRedirect("/smetalar/detal/" + id);
        }
    }
}