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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlisdanGeriQaytarmaService {

    @Autowired private AlisdanGeriQaytarmaRepository geriQaytarmaRepo;
    @Autowired private MehsulRepository mehsulRepo;
    @Autowired private AnbarRepository anbarRepo;
    @Autowired private LayiheRepository layiheRepo;
    @Autowired private KontragentRepository kontragentRepo;
    @Autowired private IstifadeciRepository istifadeciRepo;
    @Autowired private XidmetRepository xidmetRepo;
    @Autowired private AnbarMehsulRepository anbarMehsulRepo;
    @Autowired private jakarta.persistence.EntityManager entityManager;

    // İcazəli aktiv qaimələr
    public List<AlisdanGeriQaytarma> qaimeleriGetir(String username) {
        Istifadeci user = istifadeciRepo.findByUsername(username);
        List<AlisdanGeriQaytarma> butunQaimeler = geriQaytarmaRepo.findBySilinibFalseOrderByIdDesc();

        if (user != null && user.getRol().equals("ROLE_ADMIN")) return butunQaimeler;

        List<Anbar> icazeliAnbarlar = user != null ? user.getAllowedWarehouses() : new ArrayList<>();
        return butunQaimeler.stream()
                .filter(q -> icazeliAnbarlar.contains(q.getAnbar()))
                .collect(Collectors.toList());
    }

    // İcazəli arxiv qaimələr
    public List<AlisdanGeriQaytarma> arxivQaimeleriGetir(String username) {
        Istifadeci user = istifadeciRepo.findByUsername(username);
        List<AlisdanGeriQaytarma> butunQaimeler = geriQaytarmaRepo.findBySilinibTrueOrderByIdDesc();

        if (user != null && user.getRol().equals("ROLE_ADMIN")) return butunQaimeler;

        List<Anbar> icazeliAnbarlar = user != null ? user.getAllowedWarehouses() : new ArrayList<>();
        return butunQaimeler.stream()
                .filter(q -> icazeliAnbarlar.contains(q.getAnbar()))
                .collect(Collectors.toList());
    }

    // İcazəli anbarları gətirən köməkçi metod
    public List<Anbar> getIcazeliAnbarlar(String username) {
        Istifadeci user = istifadeciRepo.findByUsername(username);
        if (user != null && user.getRol().equals("ROLE_ADMIN")) return anbarRepo.findAll();
        return user != null ? user.getAllowedWarehouses() : new ArrayList<>();
    }

    @Transactional
    public void geriQaytarmaEt(LocalDate tarix, Long anbarId, Long kontragentId,
                               List<Long> mehsulIdler, List<Long> xidmetIdler, List<Long> layiheIdler,
                               List<String> novler, List<BigDecimal> miqdarlar, List<BigDecimal> qiymetler,
                               List<String> hesablar, List<String> qeydler) {

        AlisdanGeriQaytarma qaime = new AlisdanGeriQaytarma();
        qaime.setTarix(tarix);
        qaime.setAnbar(anbarRepo.findById(anbarId).orElseThrow());
        qaime.setKontragent(kontragentRepo.findById(kontragentId).orElseThrow());
        qaime.setYaradilmaTarixi(LocalDateTime.now());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            qaime.setYaradanIstifadeci(istifadeciRepo.findByUsername(auth.getName()));
        }

        AlisdanGeriQaytarma sonQaime = geriQaytarmaRepo.findTopByOrderByIdDesc();
        int yeniNomre = 1;
        if (sonQaime != null && sonQaime.getSenedNomresi() != null) {
            try { yeniNomre = Integer.parseInt(sonQaime.getSenedNomresi().replace("AGQ-", "")) + 1; }
            catch (Exception e) { yeniNomre = 1; }
        }
        qaime.setSenedNomresi(String.format("AGQ-%03d", yeniNomre));

        BigDecimal umumiMebleg = BigDecimal.ZERO;
        List<AlisdanGeriQaytarmaSetri> setirler = new java.util.ArrayList<>();

        for (int i = 0; i < miqdarlar.size(); i++) {
            AlisdanGeriQaytarmaSetri setir = new AlisdanGeriQaytarmaSetri();
            setir.setAlisdanGeriQaytarma(qaime);
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
                Mehsul secilmisMehsul = mehsulRepo.findById(mehsulIdler.get(i)).orElseThrow();
                setir.setMehsul(secilmisMehsul);

                AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(anbarId, secilmisMehsul.getId());
                double movcudMiqdar = (anbarMehsul != null && anbarMehsul.getMiqdar() != null) ? anbarMehsul.getMiqdar() : 0.0;
                double qaytarilanMiqdar = miqdarlar.get(i).doubleValue();

                if (anbarMehsul == null || movcudMiqdar < qaytarilanMiqdar) {
                    throw new RuntimeException("XƏTA: '" + secilmisMehsul.getAd() + "' məhsulundan anbarda kifayət qədər qalıq yoxdur!");
                }

                anbarMehsul.setMiqdar(movcudMiqdar - qaytarilanMiqdar);
                BigDecimal yeniQaliqDeyeri = anbarMehsul.getQaliqDeyeri().subtract(mebleg);
                anbarMehsul.setQaliqDeyeri(yeniQaliqDeyeri.compareTo(BigDecimal.ZERO) > 0 ? yeniQaliqDeyeri : BigDecimal.ZERO);
                anbarMehsulRepo.save(anbarMehsul);
            } else if ("XIDMET".equals(novler.get(i)) && xidmetIdler != null && xidmetIdler.size() > i && xidmetIdler.get(i) != null) {
                setir.setXidmet(xidmetRepo.findById(xidmetIdler.get(i)).orElse(null));
            }

            setirler.add(setir);
        }

        qaime.setUmumiMebleg(umumiMebleg);
        qaime.setSetirler(setirler);
        geriQaytarmaRepo.save(qaime);
    }

    @Transactional
    public void geriQaytarmaRedakteEt(Long id, LocalDate tarix, Long anbarId, Long kontragentId,
                                      List<Long> mehsulIdler, List<Long> xidmetIdler, List<Long> layiheIdler,
                                      List<String> novler, List<BigDecimal> miqdarlar, List<BigDecimal> qiymetler,
                                      List<String> hesablar, List<String> qeydler) {

        AlisdanGeriQaytarma qaime = geriQaytarmaRepo.findById(id).orElseThrow();

        for (AlisdanGeriQaytarmaSetri kohneSetir : qaime.getSetirler()) {
            if (kohneSetir.getMehsul() != null) {
                AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(qaime.getAnbar().getId(), kohneSetir.getMehsul().getId());
                if (anbarMehsul != null) {
                    anbarMehsul.setMiqdar(anbarMehsul.getMiqdar() + kohneSetir.getMiqdar().doubleValue());
                    anbarMehsul.setQaliqDeyeri(anbarMehsul.getQaliqDeyeri().add(kohneSetir.getMebleg()));
                    anbarMehsulRepo.save(anbarMehsul);
                }
            }
        }

        qaime.setTarix(tarix);
        qaime.setAnbar(anbarRepo.findById(anbarId).orElseThrow());
        qaime.setKontragent(kontragentRepo.findById(kontragentId).orElseThrow());
        qaime.getSetirler().clear();

        BigDecimal umumiMebleg = BigDecimal.ZERO;
        List<AlisdanGeriQaytarmaSetri> yeniSetirler = new java.util.ArrayList<>();

        for (int i = 0; i < miqdarlar.size(); i++) {
            AlisdanGeriQaytarmaSetri setir = new AlisdanGeriQaytarmaSetri();
            setir.setAlisdanGeriQaytarma(qaime);
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
                Mehsul secilmisMehsul = mehsulRepo.findById(mehsulIdler.get(i)).orElseThrow();
                setir.setMehsul(secilmisMehsul);

                AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(anbarId, secilmisMehsul.getId());
                double movcudMiqdar = (anbarMehsul != null && anbarMehsul.getMiqdar() != null) ? anbarMehsul.getMiqdar() : 0.0;
                double qaytarilanMiqdar = miqdarlar.get(i).doubleValue();

                if (anbarMehsul == null || movcudMiqdar < qaytarilanMiqdar) {
                    throw new RuntimeException("XƏTA: '" + secilmisMehsul.getAd() + "' məhsulundan anbarda kifayət qədər qalıq yoxdur!");
                }

                anbarMehsul.setMiqdar(movcudMiqdar - qaytarilanMiqdar);
                BigDecimal yeniQaliqDeyeri = anbarMehsul.getQaliqDeyeri().subtract(mebleg);
                anbarMehsul.setQaliqDeyeri(yeniQaliqDeyeri.compareTo(BigDecimal.ZERO) > 0 ? yeniQaliqDeyeri : BigDecimal.ZERO);
                anbarMehsulRepo.save(anbarMehsul);
            } else if ("XIDMET".equals(novler.get(i)) && xidmetIdler != null && xidmetIdler.size() > i && xidmetIdler.get(i) != null) {
                setir.setXidmet(xidmetRepo.findById(xidmetIdler.get(i)).orElse(null));
            }

            yeniSetirler.add(setir);
        }

        qaime.setUmumiMebleg(umumiMebleg);
        qaime.getSetirler().addAll(yeniSetirler);
        geriQaytarmaRepo.save(qaime);
    }

    @Transactional
    public void qaimeSil(Long id) {
        AlisdanGeriQaytarma qaime = geriQaytarmaRepo.findById(id).orElseThrow();

        for (AlisdanGeriQaytarmaSetri setir : qaime.getSetirler()) {
            if (setir.getMehsul() != null) {
                AnbarMehsul anbarMehsul = anbarMehsulRepo.findByAnbar_IdAndMehsul_Id(qaime.getAnbar().getId(), setir.getMehsul().getId());
                if (anbarMehsul != null) {
                    double yeniMiqdar = anbarMehsul.getMiqdar() + setir.getMiqdar().doubleValue();
                    BigDecimal yeniQaliqDeyeri = anbarMehsul.getQaliqDeyeri().add(setir.getMebleg());
                    anbarMehsul.setMiqdar(yeniMiqdar);
                    anbarMehsul.setQaliqDeyeri(yeniQaliqDeyeri);
                    if (yeniMiqdar > 0) {
                        anbarMehsul.setOrtaDeyer(yeniQaliqDeyeri.divide(BigDecimal.valueOf(yeniMiqdar), 2, RoundingMode.HALF_UP));
                    }
                    anbarMehsulRepo.save(anbarMehsul);
                }
            }
        }

        qaime.setSilinib(true);
        qaime.setSilinmeTarixi(LocalDateTime.now());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            qaime.setSilenIstifadeci(istifadeciRepo.findByUsername(auth.getName()));
        }
        geriQaytarmaRepo.save(qaime);
    }

    @Transactional
    public void qaimeBerpaEt(Long id) {
        AlisdanGeriQaytarma qaime = geriQaytarmaRepo.findById(id).orElseThrow();

        for (AlisdanGeriQaytarmaSetri setir : qaime.getSetirler()) {
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
                double yeniMiqdar = anbarMehsul.getMiqdar() - setir.getMiqdar().doubleValue();
                if (yeniMiqdar < 0) {
                    throw new RuntimeException("XƏTA: Bu qaiməni bərpa etmək mümkün deyil, anbarda kifayət qədər mal yoxdur!");
                }
                anbarMehsul.setMiqdar(yeniMiqdar);
                anbarMehsul.setQaliqDeyeri(anbarMehsul.getQaliqDeyeri().subtract(setir.getMebleg()));
                anbarMehsulRepo.save(anbarMehsul);
            }
        }

        qaime.setSilinib(false);
        qaime.setSilinmeTarixi(null);
        qaime.setSilenIstifadeci(null);
        geriQaytarmaRepo.save(qaime);
    }

    @Transactional
    public void qaimeBirdəfəlikSil(Long id) {
        entityManager.flush();
        entityManager.clear();
        entityManager.createNativeQuery("DELETE FROM alisdan_geri_qaytarma_setirleri WHERE geri_qaytarma_id = :id")
                .setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM alisdan_geri_qaytarma WHERE id = :id")
                .setParameter("id", id).executeUpdate();
    }
}