package az.maliyye.MaliyyeApp.kontragent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TehtelhesabService {

    @Autowired
    private TehtelhesabRepository repository;

    public List<TehtelhesabSexs> hamisiniGetir() {
        return repository.findAll();
    }

    public void elaveEt(TehtelhesabSexs sexs, String istifadeciAdi) {
        if (sexs.getYaradanIstifadeci() == null) {
            sexs.setYaradanIstifadeci(istifadeciAdi != null ? istifadeciAdi : "Admin");
        }
        repository.save(sexs);
    }

    public TehtelhesabSexs tapId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Təhtəlhesab şəxs tapılmadı!"));
    }

    public void redakteEt(Long id, TehtelhesabSexs yenilenmis, String istifadeciAdi) {
        TehtelhesabSexs movcud = tapId(id);
        movcud.setAdSoyad(yenilenmis.getAdSoyad());
        movcud.setFinKodu(yenilenmis.getFinKodu());
        movcud.setVesiqeNomresi(yenilenmis.getVesiqeNomresi());
        movcud.setTelefon(yenilenmis.getTelefon());
        movcud.setUnvan(yenilenmis.getUnvan());
        movcud.setVezife(yenilenmis.getVezife());
        movcud.setQeydler(yenilenmis.getQeydler());
        movcud.setDuzelenIstifadeci(istifadeciAdi != null ? istifadeciAdi : "Admin");

        repository.save(movcud);
    }

    public void sil(Long id) {
        repository.deleteById(id);
    }
}