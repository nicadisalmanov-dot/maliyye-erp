package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BankService {

    @Autowired
    private BankEmeliyyatiRepository repository;

    public List<BankEmeliyyati> hamisiniGetir() {
        return repository.findAll();
    }

    public BankEmeliyyati elaveEt(BankEmeliyyati emeliyyat) {
        return repository.save(emeliyyat);
    }

    public void sil(Long id) {
        repository.deleteById(id);
    }

    public BigDecimal medaxilCemi() {
        BigDecimal cem = BigDecimal.ZERO;
        for (BankEmeliyyati e : repository.findAll()) {
            if (e.isMedaxil()) cem = cem.add(e.getMebleg());
        }
        return cem;
    }

    public BigDecimal mexaricCemi() {
        BigDecimal cem = BigDecimal.ZERO;
        for (BankEmeliyyati e : repository.findAll()) {
            if (e.isMexaric()) cem = cem.add(e.getMebleg());
        }
        return cem;
    }

    public BigDecimal bankQaligi() {
        return medaxilCemi().subtract(mexaricCemi());
    }
}