package az.maliyye.MaliyyeApp.kontragent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class KreditorService {

    @Autowired
    private KreditorRepository kreditorRepository;

    public List<Kreditor> hamisiniGetir() {
        return kreditorRepository.findAll();
    }

    public void yaddaSaxla(Kreditor kreditor, String istifadeciAdi) {
        if (kreditor.getYaradanIstifadeci() == null) {
            kreditor.setYaradanIstifadeci(istifadeciAdi);
        }
        kreditorRepository.save(kreditor);
    }

    public void sil(Long id) {
        kreditorRepository.deleteById(id);
    }
}