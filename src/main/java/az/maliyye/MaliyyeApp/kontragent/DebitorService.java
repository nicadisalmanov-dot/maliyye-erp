package az.maliyye.MaliyyeApp.kontragent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DebitorService {

    @Autowired
    private DebitorRepository debitorRepository;

    public List<Debitor> hamisiniGetir() {
        return debitorRepository.findAll();
    }

    public void yaddaSaxla(Debitor debitor, String istifadeciAdi) {
        if (debitor.getYaradanIstifadeci() == null) {
            debitor.setYaradanIstifadeci(istifadeciAdi);
        }
        debitorRepository.save(debitor);
    }

    public void sil(Long id) {
        debitorRepository.deleteById(id);
    }
}