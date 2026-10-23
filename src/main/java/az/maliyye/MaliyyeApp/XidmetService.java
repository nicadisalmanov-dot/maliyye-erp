package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class XidmetService {

    @Autowired private XidmetQrupuRepository qrupRepo;
    @Autowired private XidmetRepository xidmetRepo;
    @Autowired private IstifadeciRepository istifadeciRepo;

    // Cari istifadəçini götürən köməkçi metod
    private Istifadeci getCariIstifadeci() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return istifadeciRepo.findByUsername(auth.getName());
        }
        return null;
    }

    // --- QRUP ---
    public List<XidmetQrupu> butunQruplar() { return qrupRepo.findAll(); }

    public XidmetQrupu qrupElaveEt(String ad) {
        XidmetQrupu yeni = new XidmetQrupu(ad);
        yeni.setYaradanIstifadeci(getCariIstifadeci());
        return qrupRepo.save(yeni);
    }

    public XidmetQrupu qrupRedakteEt(Long id, String ad) {
        XidmetQrupu q = qrupRepo.findById(id).orElseThrow();
        q.setAd(ad);
        return qrupRepo.save(q);
    }

    public void qrupSil(Long id) { qrupRepo.deleteById(id); }
    public long qrupdaXidmetSayi(Long qrupId) { return xidmetRepo.countByQrup_Id(qrupId); }

    // --- XİDMƏT ---
    public List<Xidmet> butunXidmetler() { return xidmetRepo.findAll(); }

    public Xidmet xidmetElaveEt(String ad, Long qrupId, String nov, String vahid, BigDecimal qiymet) {
        XidmetQrupu qrup = qrupRepo.findById(qrupId).orElseThrow();
        Xidmet yeni = new Xidmet(ad, qrup, nov, vahid, qiymet);
        yeni.setYaradanIstifadeci(getCariIstifadeci());
        return xidmetRepo.save(yeni);
    }

    public Xidmet xidmetRedakteEt(Long id, String ad, Long qrupId, String nov, String vahid, BigDecimal qiymet) {
        Xidmet x = xidmetRepo.findById(id).orElseThrow();
        x.setAd(ad);
        x.setQrup(qrupRepo.findById(qrupId).orElseThrow());
        x.setNov(nov);
        x.setVahid(vahid);
        x.setQiymet(qiymet);
        x.setDuzelisTarixi(LocalDateTime.now());
        return xidmetRepo.save(x);
    }

    public void xidmetSil(Long id) { xidmetRepo.deleteById(id); }
}