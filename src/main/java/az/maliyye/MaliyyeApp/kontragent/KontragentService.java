package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KontragentService {

    @Autowired
    private KontragentRepository repository;

    public List<Kontragent> hamisiniGetir() {
        return repository.findAll();
    }

    // YENİ: Yalnız Müştəriləri gətir (Müştəri və ya Hər ikisi)
    public List<Kontragent> musteriGetir() {
        return repository.findByNovIn(List.of("MUSTERI", "HER_IKISI"));
    }

    // YENİ: Yalnız Təchizatçıları gətir (Təchizatçı və ya Hər ikisi)
    public List<Kontragent> techizatciGetir() {
        return repository.findByNovIn(List.of("TECHIZATCI", "HER_IKISI"));
    }

    public void elaveEt(Kontragent kontragent) {
        repository.save(kontragent);
    }

    public void sil(Long id) {
        repository.deleteById(id);
    }

    public void redakteEt(Long id, String ad, String telefon, String email, String unvan, String nov) {
        Kontragent k = repository.findById(id).orElseThrow();
        k.setAd(ad);
        k.setTelefon(telefon);
        k.setEmail(email);
        k.setUnvan(unvan);
        k.setNov(nov);
        repository.save(k);
    }
}