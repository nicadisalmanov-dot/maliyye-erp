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

    public List<Kontragent> musteriGetir() {
        return repository.findByNov("MUSTERI");
    }

    public void elaveEt(Kontragent kontragent) {
        repository.save(kontragent);
    }

    public void sil(Long id) {
        repository.deleteById(id);
    }
}