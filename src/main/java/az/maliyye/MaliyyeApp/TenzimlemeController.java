package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tenzimlemeler")
public class TenzimlemeController {

    @Autowired private OlcuVahidiRepository olcuVahidiRepo;
    @Autowired private MalTipiRepository malTipiRepo;
    @Autowired private MehsulQrupuRepository mehsulQrupuRepo;

    @GetMapping
    public String tenzimlemelerSehife(Model model) {
        model.addAttribute("olcuVahidleri", olcuVahidiRepo.findAll());
        model.addAttribute("malTipleri", malTipiRepo.findAll());
        model.addAttribute("malQruplari", mehsulQrupuRepo.findAll());
        return "tenzimlemeler";
    }

    // --- Ölçü Vahidi Əməliyyatları ---
    @PostMapping("/olcu-vahidi-elave-et")
    public String olcuVahidiElaveEt(@RequestParam String ad, @RequestParam String qisaAd, RedirectAttributes redirectAttributes) {
        try {
            olcuVahidiRepo.save(new OlcuVahidi(ad, qisaAd));
            redirectAttributes.addFlashAttribute("ugur", "Ölçü vahidi əlavə edildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/tenzimlemeler";
    }

    @PostMapping("/olcu-vahidi-sil/{id}")
    public String olcuVahidiSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            olcuVahidiRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("ugur", "Ölçü vahidi silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Silinə bilmədi.");
        }
        return "redirect:/tenzimlemeler";
    }

    // --- Mal Tipi Əməliyyatları ---
    @PostMapping("/mal-tipi-elave-et")
    public String malTipiElaveEt(@RequestParam String ad, RedirectAttributes redirectAttributes) {
        try {
            malTipiRepo.save(new MalTipi(ad));
            redirectAttributes.addFlashAttribute("ugur", "Mal tipi əlavə edildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/tenzimlemeler";
    }

    @PostMapping("/mal-tipi-sil/{id}")
    public String malTipiSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            malTipiRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("ugur", "Mal tipi silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Silinə bilmədi.");
        }
        return "redirect:/tenzimlemeler";
    }

    // --- Mal Qrupu Əməliyyatları ---
    @PostMapping("/mal-qrupu-elave-et")
    public String malQrupuElaveEt(@RequestParam String ad, RedirectAttributes redirectAttributes) {
        try {
            mehsulQrupuRepo.save(new MehsulQrupu(ad));
            redirectAttributes.addFlashAttribute("ugur", "Mal qrupu əlavə edildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/tenzimlemeler";
    }

    @PostMapping("/mal-qrupu-sil/{id}")
    public String malQrupuSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            mehsulQrupuRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("ugur", "Mal qrupu silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: Silinə bilmədi.");
        }
        return "redirect:/tenzimlemeler";
    }
}