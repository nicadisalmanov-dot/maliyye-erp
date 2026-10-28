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
public class AlisQaimesiService {

    @Autowired private AlisQaimesiRepository alisRepo;
    @Autowired private MehsulRepository mehsulRepo;
    @Autowired private AnbarRepository anbarRepo;
    @Autowired private LayiheRepository layiheRepo;
    @Autowired private KontragentRepository kontragentRepo;
    @Autowired private IstifadeciRepository istifadeciRepo;
    @Autowired private XidmetRepository xidmetRepo;
    @Autowired private AnbarMehsulRepository anbarMehsulRepo;
    @Autowired private jakarta.persistence.EntityManager entityManager;

    public List<AlisQaimesi> qaimeleriGetir(String username) {
        Istifadeci u = istifadeciRepo.findByUsername(username);
        if (u != null && u.getRol().equals("ROLE_ADMIN")) {
            return alisRepo.findBySilinibFalseOrderByIdDesc();
        } else {
            return alisRepo.findBySilinibFalseAndYaradanIstifadeci_UsernameOrderByIdDesc(username);
        }
    }

    public List<AlisQaimesi> arxivQaimeleriGetir(String username) {
        Istifadeci u = istifadeciRepo.findByUsername(username);
        if (u != null && u.getRol().equals("ROLE_ADMIN")) {
            return alisRepo.findBySilinibTrueOrderByIdDesc();
        } else {
            return alisRepo.findBySilinibTrueAndYaradanIstifadeci_UsernameOrderByIdDesc(username);
        }
    }

    @Transactional
    public void alisEt(LocalDate tarix, Long anbarId, Long kontragentId,
                       List<Long> mehsulIdler, List<Long> xidmetIdler, List<Long> layiheIdler,
                       List<String> novler, List<BigDecimal> miqdarlar, List<BigDecimal> qiymetler,
                       List<String> hesablar, List<String> hesablar2, List<String> qeydler) {

        AlisQaimesi qaime = new AlisQaimesi();
        qaime.setTarix(tarix);
        qaime.setAnbar(anbarRepo.findById(anbarId).orElseThrow());
        qaime.setKontragent(kontragentRepo.findById(kontragentId).orElseThrow());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Istifadeci cariIstifadeci = istifadeciRepo.findByUsername(auth.getName());
            qaime.setYaradanIstifadeci(cariIstifadeci);
        }

        AlisQaimesi sonQaime = alisRepo.findTopByOrderByIdDesc();
        int yeniNomre = 1;
        if (sonQaime != null && sonQaime.getSenedNomresi() != null) {
            try { yeniNomre = Integer.parseInt(sonQaime.getSenedNomresi().replace("AL-", "")) + 1; }
            catch (Exception e) { yeniNomre = 1; }
        }
        qaime.setSenedNomresi(String.format("AL-%03d", yeniNomre));

        BigDecimal umumiMebleg = BigDecimal.ZERO;
        List<AlisQaimesiSetri> setirler = new java.util.ArrayList<>();

        for (int i = 0; i < miqdarlar.size(); i++) {
            AlisQaimesiSetri setir = new AlisQaimesiSetri();
            setir.setAlisQaimesi(qaime);
            setir.setNov(novler.get(i));

            if (layiheIdler != null && layiheIdler.size() > i && layiheIdler.get(i) != null) {
                setir.setLayihe(layiheRepo.findById(layiheIdler.get(i)).orElse(null));
            }

            setir.setMiqdar(miqdarlar.get(i));
            setir.setVahidQiymeti(qiymetler.get(i));
            setir.setHesablarPlani(hesablar != null && hesablar.size() > i ? hesablar.get(i) : null);
            setir.setHesablarPlani2(hesablar2 != null && hesablar2.size() > i ? hesablar2.get(i) : null);
            setir.setElaveQeyd(qeydler != null && qeydler.size() > i ? qeydler.get(i) : null);

            BigDecimal mebleg = miqdarlar.get(i).multiply(qiymetler.get(i));
            setir.setMebleg(mebleg);
            umumiMebleg = umumiMebleg.add(mebleg);

            if (("MAL".equals(novler.get(i)) || "ESAS_VESAIT".equals(novler.get(i)) || "QEYRI_MADDI".equals(novler.get(i)))
                    && mehsulIdler != null && mehsulIdler.size() > i && mehsulIdler.get(i) != null) {

                Mehsul secilmisMehsul = mehsulRepo.findById(mehsulIdler.get(i)).orElseThrow();
                Mehsul movcudMehsul = mehsulRepo.findByAd(secilmisMehsul.getAd());

                if (movcudMehsul != null) {
                    setir.setMehsul(movcudMehsul);
                    AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(anbarId, movcudMehsul.getId());
                    if (anbarMehsul == null) {
                        anbarMehsul = new AnbarMehsul();
                        anbarMehsul.setAnbar(anbarRepo.findById(anbarId).orElseThrow());
                        anbarMehsul.setMehsul(movcudMehsul);
                        anbarMehsul.setMiqdar(0.0);
                        anbarMehsul.setOrtaDeyer(qiymetler.get(i));
                        anbarMehsul.setQaliqDeyeri(BigDecimal.ZERO);
                    }
                    Double yeniMiqdar = anbarMehsul.getMiqdar() + miqdarlar.get(i).doubleValue();
                    BigDecimal yeniQaliqDeyeri = anbarMehsul.getQaliqDeyeri().add(mebleg);
                    BigDecimal yeniOrtaDeyer = yeniQaliqDeyeri.divide(BigDecimal.valueOf(yeniMiqdar), 2, RoundingMode.HALF_UP);
                    anbarMehsul.setMiqdar(yeniMiqdar);
                    anbarMehsul.setQaliqDeyeri(yeniQaliqDeyeri);
                    anbarMehsul.setOrtaDeyer(yeniOrtaDeyer);
                    anbarMehsulRepo.save(anbarMehsul);
                } else {
                    setir.setMehsul(secilmisMehsul);
                }
            } else if ("XIDMET".equals(novler.get(i)) && xidmetIdler != null && xidmetIdler.size() > i && xidmetIdler.get(i) != null) {
                Xidmet xidmet = xidmetRepo.findById(xidmetIdler.get(i)).orElse(null);
                setir.setXidmet(xidmet);
                setir.setMayaDeyeri(BigDecimal.ZERO);
                setir.setMenfeet(mebleg);
            }

            setirler.add(setir);
        }

        qaime.setUmumiMebleg(umumiMebleg);
        qaime.setSetirler(setirler);
        alisRepo.save(qaime);
    }

    @Transactional
    public void qaimeSil(Long id) {
        AlisQaimesi qaime = alisRepo.findById(id).orElseThrow();

        for (AlisQaimesiSetri setir : qaime.getSetirler()) {
            if (setir.getMehsul() != null) {
                AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(qaime.getAnbar().getId(), setir.getMehsul().getId());

                double movcudMiqdar = (anbarMehsul != null && anbarMehsul.getMiqdar() != null) ? anbarMehsul.getMiqdar() : 0.0;
                double qaimedekiMiqdar = setir.getMiqdar() != null ? setir.getMiqdar().doubleValue() : 0.0;

                if (movcudMiqdar < qaimedekiMiqdar) {
                    throw new RuntimeException("XƏTA: '" + setir.getMehsul().getAd() +
                            "' məhsulu artıq satılıb və ya istifadə olunub! Anbarda kifayət qədər qalıq yoxdur " +
                            "(Anbarda mövcud: " + movcudMiqdar + ", Qaimədəki: " + qaimedekiMiqdar + "). Qaiməni tam silmək olmaz, lakin redaktə edib miqdarı azalda bilərsiniz!");
                }
            }
        }

        for (AlisQaimesiSetri setir : qaime.getSetirler()) {
            if (setir.getMehsul() != null) {
                AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(qaime.getAnbar().getId(), setir.getMehsul().getId());
                if (anbarMehsul != null) {
                    Double yeniMiqdar = anbarMehsul.getMiqdar() - setir.getMiqdar().doubleValue();
                    BigDecimal yeniQaliqDeyeri = anbarMehsul.getQaliqDeyeri().subtract(setir.getMebleg());
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
        alisRepo.save(qaime);
    }

    @Transactional
    public void qaimeBerpaEt(Long id) {
        AlisQaimesi qaime = alisRepo.findById(id).orElseThrow();

        for (AlisQaimesiSetri setir : qaime.getSetirler()) {
            if (setir.getMehsul() != null) {
                AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(qaime.getAnbar().getId(), setir.getMehsul().getId());
                if (anbarMehsul == null) {
                    anbarMehsul = new AnbarMehsul();
                    anbarMehsul.setAnbar(qaime.getAnbar());
                    anbarMehsul.setMehsul(setir.getMehsul());
                    anbarMehsul.setMiqdar(0.0);
                    anbarMehsul.setOrtaDeyer(setir.getVahidQiymeti());
                    anbarMehsul.setQaliqDeyeri(BigDecimal.ZERO);
                }
                Double yeniMiqdar = anbarMehsul.getMiqdar() + setir.getMiqdar().doubleValue();
                BigDecimal yeniQaliqDeyeri = anbarMehsul.getQaliqDeyeri().add(setir.getMebleg());
                BigDecimal yeniOrtaDeyer = yeniQaliqDeyeri.divide(BigDecimal.valueOf(yeniMiqdar > 0 ? yeniMiqdar : 1), 2, RoundingMode.HALF_UP);

                anbarMehsul.setMiqdar(yeniMiqdar);
                anbarMehsul.setQaliqDeyeri(yeniQaliqDeyeri);
                anbarMehsul.setOrtaDeyer(yeniOrtaDeyer);
                anbarMehsulRepo.save(anbarMehsul);
            }
        }

        qaime.setSilinib(false);
        qaime.setSilinmeTarixi(null);
        qaime.setSilenIstifadeci(null);
        alisRepo.save(qaime);
    }

    @Transactional
    public void qaimeBirdəfəlikSil(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Istifadeci cariIstifadeci = istifadeciRepo.findByUsername(auth.getName());
            if (cariIstifadeci == null || !"ROLE_ADMIN".equals(cariIstifadeci.getRol())) {
                throw new RuntimeException("Bu əməliyyat üçün yalnız Admin səlahiyyəti tələb olunur!");
            }
        } else {
            throw new RuntimeException("İstifadəçi səlahiyyəti tapılmadı!");
        }

        entityManager.flush();
        entityManager.clear();

        jakarta.persistence.Query deleteSetirler = entityManager.createNativeQuery(
                "DELETE FROM alis_qaimesi_setirleri WHERE alis_qaimesi_id = :id");
        deleteSetirler.setParameter("id", id);
        deleteSetirler.executeUpdate();

        entityManager.flush();
        entityManager.clear();

        jakarta.persistence.Query deleteQaime = entityManager.createNativeQuery(
                "DELETE FROM alis_qaimeleri WHERE id = :id");
        deleteQaime.setParameter("id", id);
        int qaimeSayi = deleteQaime.executeUpdate();

        if (qaimeSayi == 0) {
            throw new RuntimeException("Qaimə tapılmadı! ID: " + id);
        }
    }

    @Transactional
    public void qaimeRedakteEt(Long id, LocalDate tarix, Long anbarId, Long kontragentId,
                               List<Long> mehsulIdler, List<Long> xidmetIdler, List<Long> layiheIdler,
                               List<String> novler, List<BigDecimal> miqdarlar, List<BigDecimal> qiymetler,
                               List<String> hesablar, List<String> hesablar2, List<String> qeydler) {

        AlisQaimesi qaime = alisRepo.findById(id).orElseThrow();

        List<AlisQaimesiSetri> kohneSetirler = qaime.getSetirler();
        for (AlisQaimesiSetri setir : kohneSetirler) {
            if (setir.getMehsul() != null) {
                AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(qaime.getAnbar().getId(), setir.getMehsul().getId());
                if (anbarMehsul != null) {
                    Double yeniMiqdar = anbarMehsul.getMiqdar() - setir.getMiqdar().doubleValue();
                    BigDecimal yeniQaliqDeyeri = anbarMehsul.getQaliqDeyeri().subtract(setir.getMebleg());
                    anbarMehsul.setMiqdar(yeniMiqdar);
                    anbarMehsul.setQaliqDeyeri(yeniQaliqDeyeri);
                    anbarMehsulRepo.save(anbarMehsul);
                }
            }
        }

        qaime.setTarix(tarix);
        qaime.setAnbar(anbarRepo.findById(anbarId).orElseThrow());
        qaime.setKontragent(kontragentRepo.findById(kontragentId).orElseThrow());
        qaime.setDuzelisTarixi(LocalDateTime.now());

        qaime.getSetirler().clear();

        BigDecimal umumiMebleg = BigDecimal.ZERO;
        List<AlisQaimesiSetri> yeniSetirler = new java.util.ArrayList<>();

        for (int i = 0; i < miqdarlar.size(); i++) {
            AlisQaimesiSetri setir = new AlisQaimesiSetri();
            setir.setAlisQaimesi(qaime);
            setir.setNov(novler.get(i));

            if (layiheIdler != null && layiheIdler.size() > i && layiheIdler.get(i) != null) {
                setir.setLayihe(layiheRepo.findById(layiheIdler.get(i)).orElse(null));
            }

            setir.setMiqdar(miqdarlar.get(i));
            setir.setVahidQiymeti(qiymetler.get(i));
            setir.setHesablarPlani(hesablar != null && hesablar.size() > i ? hesablar.get(i) : null);
            setir.setHesablarPlani2(hesablar2 != null && hesablar2.size() > i ? hesablar2.get(i) : null);
            setir.setElaveQeyd(qeydler != null && qeydler.size() > i ? qeydler.get(i) : null);

            BigDecimal mebleg = miqdarlar.get(i).multiply(qiymetler.get(i));
            setir.setMebleg(mebleg);
            umumiMebleg = umumiMebleg.add(mebleg);

            if (("MAL".equals(novler.get(i)) || "ESAS_VESAIT".equals(novler.get(i)) || "QEYRI_MADDI".equals(novler.get(i)))
                    && mehsulIdler != null && mehsulIdler.size() > i && mehsulIdler.get(i) != null) {

                Mehsul secilmisMehsul = mehsulRepo.findById(mehsulIdler.get(i)).orElseThrow();
                Mehsul movcudMehsul = mehsulRepo.findByAd(secilmisMehsul.getAd());

                if (movcudMehsul != null) {
                    setir.setMehsul(movcudMehsul);
                    AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(anbarId, movcudMehsul.getId());
                    if (anbarMehsul == null) {
                        anbarMehsul = new AnbarMehsul();
                        anbarMehsul.setAnbar(anbarRepo.findById(anbarId).orElseThrow());
                        anbarMehsul.setMehsul(movcudMehsul);
                        anbarMehsul.setMiqdar(0.0);
                        anbarMehsul.setOrtaDeyer(qiymetler.get(i));
                        anbarMehsul.setQaliqDeyeri(BigDecimal.ZERO);
                    }
                    Double yeniMiqdar = anbarMehsul.getMiqdar() + miqdarlar.get(i).doubleValue();

                    if (yeniMiqdar < 0) {
                        throw new RuntimeException("XƏTA: Bu qədər azalda bilməzsiniz! Çünki bu məhsuldan artıq satış edilib, anbarda kifayət qədər qalıq qalmaz.");
                    }

                    BigDecimal yeniQaliqDeyeri = anbarMehsul.getQaliqDeyeri().add(mebleg);
                    BigDecimal yeniOrtaDeyer = yeniQaliqDeyeri.divide(BigDecimal.valueOf(yeniMiqdar > 0 ? yeniMiqdar : 1), 2, RoundingMode.HALF_UP);

                    anbarMehsul.setMiqdar(yeniMiqdar);
                    anbarMehsul.setQaliqDeyeri(yeniQaliqDeyeri);
                    anbarMehsul.setOrtaDeyer(yeniOrtaDeyer);
                    anbarMehsulRepo.save(anbarMehsul);
                } else {
                    setir.setMehsul(secilmisMehsul);
                }
            } else if ("XIDMET".equals(novler.get(i)) && xidmetIdler != null && xidmetIdler.size() > i && xidmetIdler.get(i) != null) {
                Xidmet xidmet = xidmetRepo.findById(xidmetIdler.get(i)).orElse(null);
                setir.setXidmet(xidmet);
                setir.setMayaDeyeri(BigDecimal.ZERO);
                setir.setMenfeet(mebleg);
            }

            yeniSetirler.add(setir);
        }

        qaime.setUmumiMebleg(umumiMebleg);
        qaime.getSetirler().addAll(yeniSetirler);
        alisRepo.save(qaime);
    }
}