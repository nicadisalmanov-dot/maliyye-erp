package az.maliyye.MaliyyeApp.smeta;

import az.maliyye.MaliyyeApp.layiheler.LayiheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/smetalar")
public class SmetaController {

    @Autowired
    private SmetaService smetaService;

    @Autowired
    private LayiheRepository layiheRepository;

    @Autowired
    private FaktikiXercRepository faktikiXercRepository;

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
    public String smetaElaveEt(@RequestParam Long layiheId,
                               @RequestParam String ad,
                               @RequestParam String versiya,
                               @RequestParam String valyuta,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
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
    public String smetaDetal(@PathVariable Long id, Model model) {
        try {
            Smeta smeta = smetaService.smetaTap(id);
            List<SmetaBendi> bəndlər = smeta.getBəndlər();

            double umumiFaktikiXerc = 0.0;

            if (bəndlər != null) {
                for (SmetaBendi bend : bəndlər) {
                    Double faktiki = faktikiXercRepository.getCemiFaktikiXercByBendiId(bend.getId());
                    bend.setFaktikiXerc(faktiki != null ? faktiki : 0.0);
                    umumiFaktikiXerc += bend.getFaktikiXerc();
                }
            }

            model.addAttribute("smeta", smeta);
            model.addAttribute("bəndlər", bəndlər);
            model.addAttribute("umumiFaktikiXerc", umumiFaktikiXerc);

            return "smeta/smeta-detal";
        } catch (Exception e) {
            return "redirect:/smetalar";
        }
    }

    @PostMapping("/detal/{smetaId}/bend-elave-et")
    public String bendElaveEt(@PathVariable Long smetaId,
                              @RequestParam String isAdi,
                              @RequestParam String vahid,
                              @RequestParam Double miqdar,
                              @RequestParam Double vahidQiymeti,
                              @RequestParam String kateqoriya,
                              RedirectAttributes redirectAttributes) {
        try {
            smetaService.bendElaveEt(smetaId, isAdi, vahid, miqdar, vahidQiymeti, kateqoriya);
            redirectAttributes.addFlashAttribute("ugur", "Smeta bəndi (iş) əlavə olundu.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/smetalar/detal/" + smetaId;
    }

    @PostMapping("/detal/bend-sil/{smetaId}/{bendiId}")
    public String bendSil(@PathVariable Long smetaId,
                          @PathVariable Long bendiId,
                          RedirectAttributes redirectAttributes) {
        try {
            smetaService.bendSil(bendiId);
            redirectAttributes.addFlashAttribute("ugur", "Smeta bəndi silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/smetalar/detal/" + smetaId;
    }

    @PostMapping("/tesdiqle/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String budceyeCevirVeTesdiqle(@PathVariable Long id,
                                         @RequestParam(required = false) Double gozlenilmezFaiz,
                                         @RequestParam(required = false) Double dolayiFaiz,
                                         Principal principal,
                                         RedirectAttributes redirectAttributes) {
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
}