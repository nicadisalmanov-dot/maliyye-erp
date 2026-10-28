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
    @Autowired private AnbarMehsulRepository anbarMehsulRepo;

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

                AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(anbarId, mehsul.getId());
                if (anbarMehsul == null || anbarMehsul.getMiqdar() < miqdarlar.get(i).doubleValue()) {
                    throw new RuntimeException("Anbarda kifayət qədər mal yoxdur! Mövcud: " + (anbarMehsul != null ? anbarMehsul.getMiqdar() : 0));
                }

                BigDecimal mayaDeyeri = anbarMehsul.getOrtaDeyer().multiply(miqdarlar.get(i));
                setir.setMayaDeyeri(mayaDeyeri);
                setir.setMenfeet(mebleg.subtract(mayaDeyeri));

                Double yeniMiqdar = anbarMehsul.getMiqdar() - miqdarlar.get(i).doubleValue();
                BigDecimal yeniQaliqDeyeri = anbarMehsul.getQaliqDeyeri().subtract(mayaDeyeri);

                anbarMehsul.setMiqdar(yeniMiqdar);
                anbarMehsul.setQaliqDeyeri(yeniQaliqDeyeri);
                anbarMehsulRepo.save(anbarMehsul);
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

        if (qaime.getSilinib() == null || !qaime.getSilinib()) {
            for (SatisQaimesiSetri setir : qaime.getSetirler()) {
                if (setir.getMehsul() != null) {
                    AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(qaime.getAnbar().getId(), setir.getMehsul().getId());
                    if (anbarMehsul != null) {
                        Double yeniMiqdar = anbarMehsul.getMiqdar() + setir.getMiqdar().doubleValue();
                        BigDecimal yeniQaliqDeyeri = anbarMehsul.getQaliqDeyeri().add(setir.getMayaDeyeri() != null ? setir.getMayaDeyeri() : BigDecimal.ZERO);
                        anbarMehsul.setMiqdar(yeniMiqdar);
                        anbarMehsul.setQaliqDeyeri(yeniQaliqDeyeri);
                        anbarMehsulRepo.save(anbarMehsul);
                    }
                }
            }
            qaime.setSilinib(true);
            qaime.setSilinmeTarixi(LocalDateTime.now());

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                Istifadeci silenIstifadeci = istifadeciRepo.findByUsername(auth.getName());
                qaime.setSilenIstifadeci(silenIstifadeci);
            }
            satisRepo.save(qaime);
        }
    }

    @Transactional
    public void qaimeBerpaEt(Long id) {
        SatisQaimesi qaime = satisRepo.findById(id).orElseThrow();

        if (qaime.getSilinib() != null && qaime.getSilinib()) {
            for (SatisQaimesiSetri setir : qaime.getSetirler()) {
                if (setir.getMehsul() != null) {
                    AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(qaime.getAnbar().getId(), setir.getMehsul().getId());
                    if (anbarMehsul == null || anbarMehsul.getMiqdar() < setir.getMiqdar().doubleValue()) {
                        throw new RuntimeException("Anbarda kifayət qədər mal yoxdur! Qaiməni bərpa etmək mümkün deyil. Mövcud qalıq: " + (anbarMehsul != null ? anbarMehsul.getMiqdar() : 0));
                    }

                    Double yeniMiqdar = anbarMehsul.getMiqdar() - setir.getMiqdar().doubleValue();
                    BigDecimal mayaDeyeri = setir.getMayaDeyeri() != null ? setir.getMayaDeyeri() : BigDecimal.ZERO;
                    BigDecimal yeniQaliqDeyeri = anbarMehsul.getQaliqDeyeri().subtract(mayaDeyeri);

                    anbarMehsul.setMiqdar(yeniMiqdar);
                    anbarMehsul.setQaliqDeyeri(yeniQaliqDeyeri);
                    anbarMehsulRepo.save(anbarMehsul);
                }
            }

            qaime.setSilinib(false);
            qaime.setSilinmeTarixi(null);
            qaime.setSilenIstifadeci(null);
            satisRepo.save(qaime);
        }
    }

    @Transactional
    public void qaimeBirdəfəlikSil(Long id) {
        SatisQaimesi qaime = satisRepo.findById(id).orElseThrow();
        if (qaime.getSilinib() == null || !qaime.getSilinib()) {
            for (SatisQaimesiSetri setir : qaime.getSetirler()) {
                if (setir.getMehsul() != null) {
                    AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(qaime.getAnbar().getId(), setir.getMehsul().getId());
                    if (anbarMehsul != null) {
                        Double yeniMiqdar = anbarMehsul.getMiqdar() + setir.getMiqdar().doubleValue();
                        BigDecimal yeniQaliqDeyeri = anbarMehsul.getQaliqDeyeri().add(setir.getMayaDeyeri() != null ? setir.getMayaDeyeri() : BigDecimal.ZERO);
                        anbarMehsul.setMiqdar(yeniMiqdar);
                        anbarMehsul.setQaliqDeyeri(yeniQaliqDeyeri);
                        anbarMehsulRepo.save(anbarMehsul);
                    }
                }
            }
        }
        satisRepo.deleteById(id);
    }

    @Transactional
    public void qaimeRedakteEt(Long id, LocalDate tarix, Long anbarId, Long kontragentId,
                               List<Long> mehsulIdler, List<Long> xidmetIdler, List<Long> layiheIdler,
                               List<String> novler, List<BigDecimal> miqdarlar, List<BigDecimal> qiymetler,
                               List<String> hesablar, List<String> qeydler) {

        SatisQaimesi qaime = satisRepo.findById(id).orElseThrow();

        if (qaime.getSilinib() == null || !qaime.getSilinib()) {
            List<SatisQaimesiSetri> kohneSetirler = qaime.getSetirler();
            for (SatisQaimesiSetri setir : kohneSetirler) {
                if (setir.getMehsul() != null) {
                    AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(qaime.getAnbar().getId(), setir.getMehsul().getId());
                    if (anbarMehsul != null) {
                        Double yeniMiqdar = anbarMehsul.getMiqdar() + setir.getMiqdar().doubleValue();
                        BigDecimal yeniQaliqDeyeri = anbarMehsul.getQaliqDeyeri().add(setir.getMayaDeyeri() != null ? setir.getMayaDeyeri() : BigDecimal.ZERO);
                        anbarMehsul.setMiqdar(yeniMiqdar);
                        anbarMehsul.setQaliqDeyeri(yeniQaliqDeyeri);
                        anbarMehsulRepo.save(anbarMehsul);
                    }
                }
            }
        }

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

                AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(anbarId, mehsul.getId());
                if (anbarMehsul == null || anbarMehsul.getMiqdar() < miqdarlar.get(i).doubleValue()) {
                    throw new RuntimeException("Anbarda kifayət qədər mal yoxdur! Mövcud: " + (anbarMehsul != null ? anbarMehsul.getMiqdar() : 0));
                }

                BigDecimal mayaDeyeri = anbarMehsul.getOrtaDeyer().multiply(miqdarlar.get(i));
                setir.setMayaDeyeri(mayaDeyeri);
                setir.setMenfeet(mebleg.subtract(mayaDeyeri));

                Double yeniMiqdar = anbarMehsul.getMiqdar() - miqdarlar.get(i).doubleValue();
                BigDecimal yeniQaliqDeyeri = anbarMehsul.getQaliqDeyeri().subtract(mayaDeyeri);

                anbarMehsul.setMiqdar(yeniMiqdar);
                anbarMehsul.setQaliqDeyeri(yeniQaliqDeyeri);
                anbarMehsulRepo.save(anbarMehsul);
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