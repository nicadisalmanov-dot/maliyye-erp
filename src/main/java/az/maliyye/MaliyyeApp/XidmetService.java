package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class XidmetService {

    @Autowired private XidmetQrupuRepository qrupRepo;
    @Autowired private XidmetTipiRepository tipRepo;
    @Autowired private XidmetRepository xidmetRepo;
    @Autowired private IstifadeciRepository istifadeciRepo;

    public List<XidmetQrupu> butunQruplar() { return qrupRepo.findAll(); }
    public List<XidmetTipi> butunTipler() { return tipRepo.findAll(); }
    public List<Xidmet> butunXidmetler() { return xidmetRepo.findAll(); }

    @Transactional
    public XidmetQrupu qrupElaveEt(String ad) {
        return qrupRepo.save(new XidmetQrupu(ad));
    }

    @Transactional
    public void qrupSil(Long id) {
        qrupRepo.deleteById(id);
    }

    public long qrupdaXidmetSayi(Long qrupId) {
        return xidmetRepo.countByQrup_Id(qrupId);
    }

    @Transactional
    public XidmetTipi tipElaveEt(String ad) {
        return tipRepo.save(new XidmetTipi(ad));
    }

    @Transactional
    public void tipSil(Long id) {
        tipRepo.deleteById(id);
    }

    @Transactional
    public Xidmet xidmetElaveEt(String ad, Long qrupId, Long tipId, String vahid, BigDecimal qiymet) {
        XidmetQrupu qrup = qrupRepo.findById(qrupId).orElseThrow();
        XidmetTipi tip = (tipId != null) ? tipRepo.findById(tipId).orElse(null) : null;

        Xidmet xidmet = new Xidmet(ad, qrup, tip, vahid, qiymet);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Istifadeci istifadeci = istifadeciRepo.findByUsername(auth.getName());
            xidmet.setYaradanIstifadeci(istifadeci);
        }

        return xidmetRepo.save(xidmet);
    }

    @Transactional
    public void xidmetRedakteEt(Long id, String ad, Long qrupId, Long tipId, String vahid, BigDecimal qiymet) {
        Xidmet xidmet = xidmetRepo.findById(id).orElseThrow();
        XidmetQrupu qrup = qrupRepo.findById(qrupId).orElseThrow();
        XidmetTipi tip = (tipId != null) ? tipRepo.findById(tipId).orElse(null) : null;

        xidmet.setAd(ad);
        xidmet.setQrup(qrup);
        xidmet.setTip(tip);
        xidmet.setVahid(vahid);
        xidmet.setQiymet(qiymet);

        xidmetRepo.save(xidmet);
    }

    @Transactional
    public void xidmetSil(Long id) {
        xidmetRepo.deleteById(id);
    }
}