package az.maliyye.MaliyyeApp.kontragent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class KontragentService {

    @Autowired
    private KontragentRepository kontragentRepository;

    // Bütün kontragentləri qaytarır (Kassa, Bank və s. kontrollerlər üçün)
    public List<Kontragent> hamisiniGetir() {
        return kontragentRepository.findAll();
    }

    public List<Kontragent> getKontragentlerByNov(String nov) {
        return kontragentRepository.findByKontragentNovu(nov);
    }

    public List<Kontragent> techizatciGetir() {
        return getKontragentlerByNov("TECHIZATCI");
    }

    public List<Kontragent> musteriGetir() {
        return getKontragentlerByNov("MUSTERI");
    }

    public Kontragent tapId(Long id) {
        return kontragentRepository.findById(id).orElseThrow(() -> new RuntimeException("Kontragent tapılmadı!"));
    }

    public void elaveEt(Kontragent kontragent, String istifadeci) {
        kontragent.setYaradanIstifadeci(istifadeci);
        kontragent.setYaradilmaTarixi(LocalDateTime.now());
        kontragentRepository.save(kontragent);
    }

    public void redakteEt(Long id, Kontragent yeni, String istifadeci) {
        Kontragent movcud = tapId(id);
        movcud.setSubyektNovu(yeni.getSubyektNovu());
        movcud.setAdSoyad(yeni.getAdSoyad());
        movcud.setFinKodu(yeni.getFinKodu());
        movcud.setVesiqeNomresi(yeni.getVesiqeNomresi());
        movcud.setSirketAdi(yeni.getSirketAdi());
        movcud.setVoen(yeni.getVoen());
        movcud.setTelefon(yeni.getTelefon());
        movcud.setEmail(yeni.getEmail());
        movcud.setUnvan(yeni.getUnvan());

        movcud.setDuzelenIstifadeci(istifadeci);
        movcud.setDuzelisTarixi(LocalDateTime.now());

        kontragentRepository.save(movcud);
    }

    public void sil(Long id) {
        kontragentRepository.deleteById(id);
    }
}