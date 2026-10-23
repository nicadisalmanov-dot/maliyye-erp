package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SatisQaimesiService {

    @Autowired private SatisQaimesiRepository satisRepo;
    @Autowired private MehsulRepository mehsulRepo;
    @Autowired private AnbarRepository anbarRepo;
    @Autowired private LayiheRepository layiheRepo;
    @Autowired private KontragentRepository kontragentRepo;
    @Autowired private IstifadeciRepository istifadeciRepo;
    @Autowired private XidmetRepository xidmetRepo;

    public List<SatisQaimesi> qaimeleriGetir(String username) {
        Istifadeci u = istifadeciRepo.findByUsername(username);
        if (u != null && u.getRol().equals("ROLE_ADMIN")) {
            return satisRepo.findBySilinibFalseOrderByIdDesc();
        } else {
            return satisRepo.findBySilinibFalseAndYaradanIstifadeci_UsernameOrderByIdDesc(username);
        }
    }

    public List<SatisQaimesi> arxivQaimeleriGetir(String username) {
        Istifadeci u = istifadeciRepo.findByUsername(username);
        if (u != null && u.getRol().equals("ROLE_ADMIN")) {
            return satisRepo.findBySilinibTrueOrderByIdDesc();
        } else {
            return satisRepo.findBySilinibTrueAndYaradanIstifadeci_UsernameOrderByIdDesc(username);
        }
    }

    @Transactional
    public void satisEt(LocalDate tarix, Long anbarId, Long kontragentId,
                        List<Long> mehsulIdler, List<Long> xidmetIdler, List<Long> layiheIdler,
                        List<String> novler, List<BigDecimal> miqdarlar, List<BigDecimal> qiymetler,
                        List<String> hesablar, List<String> qeydler) {

        SatisQaimesi qaime = new SatisQaimesi();
        qaime.setTarix(tarix);
        qaime.setAnbar(anbarRepo.findById(anbarId).orElseThrow());
        qaime.setKontragent(kontragentRepo.findById(kontragentId).orElseThrow());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Istifadeci cariIstifadeci = istifadeciRepo.findByUsername(auth.getName());
            qaime.setYaradanIstifadeci(cariIstifadeci);
        }

        SatisQaimesi sonQaime = satisRepo.findTopByOrderByIdDesc();
        int yeniNomre = 1;
        if (sonQaime != null && sonQaime.getSenedNomresi() != null) {
            try { yeniNomre = Integer.parseInt(sonQaime.getSenedNomresi().replace("ST-", "")) + 1; }
            catch (Exception e) { yeniNomre = 1; }
        }
        qaime.setSenedNomresi(String.format("ST-%03d", yeniNomre));

        BigDecimal umumiMebleg = BigDecimal.ZERO;
        List<SatisQaimesiSetri> setirler = new java.util.ArrayList<>();

        for (int i = 0; i < miqdarlar.size(); i++) {
            SatisQaimesiSetri setir = new SatisQaimesiSetri();
            setir.setSatisQaimesi(qaime);
            setir.setNov(novler.get(i));

            if (layiheIdler != null && layiheIdler.size() > i && layiheIdler.get(i) != null) {
                setir.setLayihe(layiheRepo.findById(layiheIdler.get(i)).orElse(null));
            }

            setir.setMiqdar(miqdarlar.get(i));
            setir.setVahidQiymeti(qiymetler.get(i));
            setir.setHesablarPlani(hesablar != null && hesablar.size() > i ? hesablar.get(i) : null);
            setir.setElaveQeyd(qeydler != null && qeydler.size() > i ? qeydler.get(i) : null);

            BigDecimal mebleg = miqdarlar.get(i).multiply(qiymetler.get(i));
            setir.setMebleg(mebleg);
            umumiMebleg = umumiMebleg.add(mebleg);

            if ("MAL".equals(novler.get(i)) && mehsulIdler != null && mehsulIdler.size() > i && mehsulIdler.get(i) != null) {
                Mehsul mehsul = mehsulRepo.findById(mehsulIdler.get(i)).orElseThrow();
                setir.setMehsul(mehsul);

                if (mehsul.getMiqdar() < miqdarlar.get(i).doubleValue()) {
                    throw new RuntimeException("Anbarda kifayət qədər mal yoxdur! Mövcud: " + mehsul.getMiqdar());
                }

                BigDecimal mayaDeyeri = mehsul.getOrtaDeyer().multiply(miqdarlar.get(i));
                setir.setMayaDeyeri(mayaDeyeri);
                setir.setMenfeet(mebleg.subtract(mayaDeyeri));

                Double yeniMiqdar = mehsul.getMiqdar() - miqdarlar.get(i).doubleValue();
                BigDecimal yeniQaliqDeyeri = mehsul.getQaliqDeyeri().subtract(mayaDeyeri);

                mehsul.setMiqdar(yeniMiqdar);
                mehsul.setQaliqDeyeri(yeniQaliqDeyeri);
                mehsulRepo.save(mehsul);
            } else if ("XIDMET".equals(novler.get(i)) && xidmetIdler != null && xidmetIdler.size() > i && xidmetIdler.get(i) != null) {
                Xidmet xidmet = xidmetRepo.findById(xidmetIdler.get(i)).orElseThrow();
                setir.setXidmet(xidmet);
                setir.setMayaDeyeri(BigDecimal.ZERO);
                setir.setMenfeet(mebleg);
            }

            setirler.add(setir);
        }

        qaime.setUmumiMebleg(umumiMebleg);
        qaime.setSetirler(setirler);
        satisRepo.save(qaime);
    }

    @Transactional
    public void qaimeSil(Long id) {
        SatisQaimesi qaime = satisRepo.findById(id).orElseThrow();
        qaime.setSilinib(true);
        qaime.setSilinmeTarixi(LocalDateTime.now());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Istifadeci silenIstifadeci = istifadeciRepo.findByUsername(auth.getName());
            qaime.setSilenIstifadeci(silenIstifadeci);
        }
        satisRepo.save(qaime);
    }

    // YENİ: Bərpa et
    @Transactional
    public void qaimeBerpaEt(Long id) {
        SatisQaimesi qaime = satisRepo.findById(id).orElseThrow();
        qaime.setSilinib(false);
        qaime.setSilinmeTarixi(null);
        qaime.setSilenIstifadeci(null);
        satisRepo.save(qaime);
    }

    // YENİ: Redaktə et
    @Transactional
    public void qaimeRedakteEt(Long id, LocalDate tarix, Long anbarId, Long kontragentId,
                               List<Long> mehsulIdler, List<Long> xidmetIdler, List<Long> layiheIdler,
                               List<String> novler, List<BigDecimal> miqdarlar, List<BigDecimal> qiymetler,
                               List<String> hesablar, List<String> qeydler) {

        SatisQaimesi qaime = satisRepo.findById(id).orElseThrow();

        qaime.setTarix(tarix);
        qaime.setAnbar(anbarRepo.findById(anbarId).orElseThrow());
        qaime.setKontragent(kontragentRepo.findById(kontragentId).orElseThrow());
        qaime.setDuzelisTarixi(LocalDateTime.now());

        qaime.getSetirler().clear();

        BigDecimal umumiMebleg = BigDecimal.ZERO;

        for (int i = 0; i < miqdarlar.size(); i++) {
            SatisQaimesiSetri setir = new SatisQaimesiSetri();
            setir.setSatisQaimesi(qaime);
            setir.setNov(novler.get(i));

            if (layiheIdler != null && layiheIdler.size() > i && layiheIdler.get(i) != null) {
                setir.setLayihe(layiheRepo.findById(layiheIdler.get(i)).orElse(null));
            }

            setir.setMiqdar(miqdarlar.get(i));
            setir.setVahidQiymeti(qiymetler.get(i));
            setir.setHesablarPlani(hesablar != null && hesablar.size() > i ? hesablar.get(i) : null);
            setir.setElaveQeyd(qeydler != null && qeydler.size() > i ? qeydler.get(i) : null);

            BigDecimal mebleg = miqdarlar.get(i).multiply(qiymetler.get(i));
            setir.setMebleg(mebleg);
            umumiMebleg = umumiMebleg.add(mebleg);

            if ("MAL".equals(novler.get(i)) && mehsulIdler != null && mehsulIdler.size() > i && mehsulIdler.get(i) != null) {
                Mehsul mehsul = mehsulRepo.findById(mehsulIdler.get(i)).orElseThrow();
                setir.setMehsul(mehsul);

                BigDecimal mayaDeyeri = mehsul.getOrtaDeyer().multiply(miqdarlar.get(i));
                setir.setMayaDeyeri(mayaDeyeri);
                setir.setMenfeet(mebleg.subtract(mayaDeyeri));

                Double yeniMiqdar = mehsul.getMiqdar() - miqdarlar.get(i).doubleValue();
                BigDecimal yeniQaliqDeyeri = mehsul.getQaliqDeyeri().subtract(mayaDeyeri);
                mehsul.setMiqdar(yeniMiqdar);
                mehsul.setQaliqDeyeri(yeniQaliqDeyeri);
                mehsulRepo.save(mehsul);
            } else if ("XIDMET".equals(novler.get(i)) && xidmetIdler != null && xidmetIdler.size() > i && xidmetIdler.get(i) != null) {
                Xidmet xidmet = xidmetRepo.findById(xidmetIdler.get(i)).orElseThrow();
                setir.setXidmet(xidmet);
                setir.setMayaDeyeri(BigDecimal.ZERO);
                setir.setMenfeet(mebleg);
            }

            qaime.getSetirler().add(setir);
        }

        qaime.setUmumiMebleg(umumiMebleg);
        satisRepo.save(qaime);
    }
}