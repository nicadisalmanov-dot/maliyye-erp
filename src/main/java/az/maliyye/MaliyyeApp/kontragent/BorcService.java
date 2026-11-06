package az.maliyye.MaliyyeApp.kontragent;

import az.maliyye.MaliyyeApp.kontragent.BorcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorcService {

    @Autowired
    private BorcRepository borcRepository;

    public List<BorcMuqavilesi> hamisiniGetir() {
        return borcRepository.findAll();
    }

    public BorcMuqavilesi tapId(Long id) {
        return borcRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borc müqaviləsi tapılmadı!"));
    }

    public void elaveEt(BorcMuqavilesi muqavile, String istifadeci) {
        muqavile.setYaradanIstifadeci(istifadeci);
        muqavile.setYaradilmaTarixi(LocalDateTime.now());
        borcRepository.save(muqavile);
    }

    public void redakteEt(Long id, BorcMuqavilesi yeni, String istifadeci) {
        BorcMuqavilesi movcud = tapId(id);

        movcud.setSubyektNovu(yeni.getSubyektNovu());
        movcud.setBorcNovu(yeni.getBorcNovu());
        movcud.setMuqavileNomresi(yeni.getMuqavileNomresi());
        movcud.setAdSoyad(yeni.getAdSoyad());
        movcud.setFinKodu(yeni.getFinKodu());
        movcud.setVesiqeNomresi(yeni.getVesiqeNomresi());

        // DÜZƏLİŞ: getSirketAdi (ə hərfi olmadan)
        movcud.setSirketAdi(yeni.getSirketAdi());

        movcud.setVoen(yeni.getVoen());
        movcud.setTelefon(yeni.getTelefon());
        movcud.setUnvan(yeni.getUnvan());
        movcud.setMebleq(yeni.getMebleq());
        movcud.setBaglanmaTarixi(yeni.getBaglanmaTarixi());
        movcud.setBitmeTarixi(yeni.getBitmeTarixi());
        movcud.setQeydler(yeni.getQeydler());

        movcud.setDuzelenIstifadeci(istifadeci);
        movcud.setDuzelisTarixi(LocalDateTime.now());

        borcRepository.save(movcud);
    }

    public void sil(Long id) {
        borcRepository.deleteById(id);
    }
}