package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/anbar")
public class XidmetController {

    @Autowired private XidmetService service;
    @Autowired private OlcuVahidiRepository olcuVahidiRepo;
    @Autowired private XidmetQrupuRepository qrupRepo;
    @Autowired private XidmetTipiRepository tipRepo;
    @Autowired private XidmetRepository xidmetRepo;

    // --- XİDMƏTLƏR ƏSAS SƏHİFƏSİ ---
    @GetMapping({"/xidmetler", "/xidmet"})
    public String xidmetlerSehife(Model model) {
        model.addAttribute("qruplar", service.butunQruplar());
        model.addAttribute("tipler", service.butunTipler());
        model.addAttribute("xidmetler", service.butunXidmetler());
        model.addAttribute("olcuVahidleri", olcuVahidiRepo.findAll());
        return "xidmetler";
    }

    // --- XİDMƏT QRUPLARI SƏHİFƏSİ VƏ ƏMƏLİYYATLARI ---
    @GetMapping({"/xidmet-qrupu", "/xidmetin-qrupu"})
    public String xidmetQrupuSehife(Model model) {
        model.addAttribute("qruplar", service.butunQruplar());
        return "xidmet-qrupu";
    }

    @PostMapping("/xidmet-qrup-elave-et")
    public String qrupElaveEt(@RequestParam String qrupAdi, RedirectAttributes redirectAttributes) {
        if(qrupAdi != null && !qrupAdi.trim().isEmpty()) {
            service.qrupElaveEt(qrupAdi.trim());
            redirectAttributes.addFlashAttribute("ugur", "Xidmət qrupu əlavə edildi!");
        }
        return "redirect:/anbar/xidmet-qrupu";
    }

    @GetMapping("/xidmet-qrupu-redakte/{id}")
    public String qrupRedakteSehife(@PathVariable Long id, Model model) {
        XidmetQrupu qrup = qrupRepo.findById(id).orElseThrow();
        model.addAttribute("qrup", qrup);
        return "xidmet-qrupu-redakte";
    }

    @PostMapping("/xidmet-qrupu-redakte/{id}")
    public String qrupRedakteEt(@PathVariable Long id, @RequestParam String qrupAdi, RedirectAttributes redirectAttributes) {
        try {
            XidmetQrupu qrup = qrupRepo.findById(id).orElseThrow();
            qrup.setAd(qrupAdi);
            qrupRepo.save(qrup);
            redirectAttributes.addFlashAttribute("ugur", "Xidmət qrupu yeniləndi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/xidmet-qrupu";
    }

    @PostMapping("/xidmet-qrup-sil/{id}")
    public String qrupSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            long say = service.qrupdaXidmetSayi(id);
            if (say > 0) {
                redirectAttributes.addFlashAttribute("xeta", "XƏTA: Bu qrupda " + say + " xidmət var!");
                return "redirect:/anbar/xidmet-qrupu";
            }
            service.qrupSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Xidmət qrupu silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/xidmet-qrupu";
    }

    // --- XİDMƏT TİPLƏRİ SƏHİFƏSİ VƏ ƏMƏLİYYATLARI ---
    @GetMapping({"/xidmet-tipi", "/xidmetin-tipi"})
    public String xidmetTipiSehife(Model model) {
        model.addAttribute("tipler", service.butunTipler());
        return "xidmet-tipi";
    }

    @PostMapping("/xidmet-tipi-elave-et")
    public String tipElaveEt(@RequestParam String tipAdi, RedirectAttributes redirectAttributes) {
        if(tipAdi != null && !tipAdi.trim().isEmpty()) {
            service.tipElaveEt(tipAdi.trim());
            redirectAttributes.addFlashAttribute("ugur", "Xidmət tipi əlavə edildi!");
        }
        return "redirect:/anbar/xidmet-tipi";
    }

    @GetMapping("/xidmet-tipi-redakte/{id}")
    public String tipRedakteSehife(@PathVariable Long id, Model model) {
        XidmetTipi tip = tipRepo.findById(id).orElseThrow();
        model.addAttribute("tip", tip);
        return "xidmet-tipi-redakte";
    }

    @PostMapping("/xidmet-tipi-redakte/{id}")
    public String tipRedakteEt(@PathVariable Long id, @RequestParam String tipAdi, RedirectAttributes redirectAttributes) {
        try {
            XidmetTipi tip = tipRepo.findById(id).orElseThrow();
            tip.setAd(tipAdi);
            tipRepo.save(tip);
            redirectAttributes.addFlashAttribute("ugur", "Xidmət tipi yeniləndi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/xidmet-tipi";
    }

    @PostMapping("/xidmet-tipi-sil/{id}")
    public String tipSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.tipSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Xidmət tipi silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/xidmet-tipi";
    }

    // --- XİDMƏT ƏLAVƏ ET / REDAKTƏ / SİL ---
    @PostMapping("/xidmet-elave-et")
    public String xidmetElaveEt(@RequestParam String xidmetAdi,
                                @RequestParam Long qrupId,
                                @RequestParam(required = false) Long tipId,
                                @RequestParam String vahid,
                                @RequestParam String qiymet,
                                RedirectAttributes redirectAttributes) {
        try {
            BigDecimal qiymetDec = new BigDecimal(qiymet);
            service.xidmetElaveEt(xidmetAdi, qrupId, tipId, vahid, qiymetDec);
            redirectAttributes.addFlashAttribute("ugur", "Xidmət uğurla əlavə edildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/xidmetler";
    }

    @GetMapping("/xidmet-redakte/{id}")
    public String xidmetRedakteSehife(@PathVariable Long id, Model model) {
        Xidmet xidmet = xidmetRepo.findById(id).orElseThrow();
        model.addAttribute("xidmet", xidmet);
        model.addAttribute("qruplar", service.butunQruplar());
        model.addAttribute("tipler", service.butunTipler());
        model.addAttribute("olcuVahidleri", olcuVahidiRepo.findAll());
        return "xidmet-redakte";
    }

    @PostMapping("/xidmet-redakte/{id}")
    public String xidmetRedakteEt(@PathVariable Long id,
                                  @RequestParam String xidmetAdi,
                                  @RequestParam Long qrupId,
                                  @RequestParam(required = false) Long tipId,
                                  @RequestParam String vahid,
                                  @RequestParam String qiymet,
                                  RedirectAttributes redirectAttributes) {
        try {
            BigDecimal qiymetDec = new BigDecimal(qiymet);
            service.xidmetRedakteEt(id, xidmetAdi, qrupId, tipId, vahid, qiymetDec);
            redirectAttributes.addFlashAttribute("ugur", "Xidmət uğurla yeniləndi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/xidmetler";
    }

    @PostMapping("/xidmet-sil/{id}")
    public String xidmetSil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.xidmetSil(id);
            redirectAttributes.addFlashAttribute("ugur", "Xidmət silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("xeta", "XƏTA: " + e.getMessage());
        }
        return "redirect:/anbar/xidmetler";
    }
}