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

    @Autowired private AlisQaimesiRepository alisQaimesiRepo;
    @Autowired private MehsulRepository mehsulRepo;
    @Autowired private AnbarRepository anbarRepo;
    @Autowired private LayiheRepository layiheRepo;
    @Autowired private KontragentRepository kontragentRepo;
    @Autowired private IstifadeciRepository istifadeciRepo; // İstifadəçi üçün

    // İstifadəçinin roluna və adına görə filtrli qaimələr
    public List<AlisQaimesi> qaimeleriGetir(String username) {
        Istifadeci u = istifadeciRepo.findByUsername(username);
        if (u != null && u.getRol().equals("ROLE_ADMIN")) {
            // Admin hamısını görür
            return alisQaimesiRepo.findBySilinibFalseOrderByIdDesc();
        } else {
            // Adi istifadəçi yalnız özününküləri görür
            return alisQaimesiRepo.findBySilinibFalseAndYaradanIstifadeci_UsernameOrderByIdDesc(username);
        }
    }

    // Arxiv üçün də eyni məntiq
    public List<AlisQaimesi> arxivQaimeleriGetir(String username) {
        Istifadeci u = istifadeciRepo.findByUsername(username);
        if (u != null && u.getRol().equals("ROLE_ADMIN")) {
            return alisQaimesiRepo.findBySilinibTrueOrderByIdDesc();
        } else {
            return alisQaimesiRepo.findBySilinibTrueAndYaradanIstifadeci_UsernameOrderByIdDesc(username);
        }
    }

    // Yeni qaimə yaratmaq
    @Transactional
    public void alisEt(LocalDate tarix, Long anbarId, Long kontragentId,
                       List<Long> mehsulIdler, List<String> xidmetAdlari, List<Long> layiheIdler,
                       List<String> novler, List<BigDecimal> miqdarlar, List<BigDecimal> qiymetler,
                       List<String> hesablar, List<String> hesablar2, List<String> qeydler) {

        AlisQaimesi qaime = new AlisQaimesi();
        qaime.setTarix(tarix);
        qaime.setAnbar(anbarRepo.findById(anbarId).orElseThrow());
        qaime.setKontragent(kontragentRepo.findById(kontragentId).orElseThrow());

        // Cari istifadəçini təyin edirik
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Istifadeci cariIstifadeci = istifadeciRepo.findByUsername(auth.getName());
            qaime.setYaradanIstifadeci(cariIstifadeci);
        }

        // Sənəd nömrəsi
        AlisQaimesi sonQaime = alisQaimesiRepo.findTopByOrderByIdDesc();
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

            if ("MAL".equals(novler.get(i)) && mehsulIdler != null && mehsulIdler.size() > i && mehsulIdler.get(i) != null) {
                Mehsul mehsul = mehsulRepo.findById(mehsulIdler.get(i)).orElseThrow();
                setir.setMehsul(mehsul);

                Double kohneMiqdar = mehsul.getMiqdar();
                BigDecimal kohneQaliqDeyeri = mehsul.getQaliqDeyeri() != null ? mehsul.getQaliqDeyeri() : BigDecimal.ZERO;

                Double yeniMiqdar = kohneMiqdar + miqdarlar.get(i).doubleValue();
                BigDecimal yeniQaliqDeyeri = kohneQaliqDeyeri.add(mebleg);
                BigDecimal yeniOrtaDeyer = yeniQaliqDeyeri.divide(BigDecimal.valueOf(yeniMiqdar), 2, RoundingMode.HALF_UP);

                mehsul.setMiqdar(yeniMiqdar);
                mehsul.setQaliqDeyeri(yeniQaliqDeyeri);
                mehsul.setOrtaDeyer(yeniOrtaDeyer);
                mehsulRepo.save(mehsul);
            } else if ("XIDMET".equals(novler.get(i))) {
                setir.setXidmetAdi(xidmetAdlari != null && xidmetAdlari.size() > i ? xidmetAdlari.get(i) : null);
            }

            setirler.add(setir);
        }

        qaime.setUmumiMebleg(umumiMebleg);
        qaime.setSetirler(setirler);
        alisQaimesiRepo.save(qaime);
    }

    // Qaiməni redaktə et
    @Transactional
    public void qaimeRedakteEt(Long id, LocalDate tarix, Long anbarId, Long kontragentId,
                               List<Long> mehsulIdler, List<String> xidmetAdlari, List<Long> layiheIdler,
                               List<String> novler, List<BigDecimal> miqdarlar, List<BigDecimal> qiymetler,
                               List<String> hesablar, List<String> hesablar2, List<String> qeydler) {

        AlisQaimesi qaime = alisQaimesiRepo.findById(id).orElseThrow();

        qaime.setTarix(tarix);
        qaime.setAnbar(anbarRepo.findById(anbarId).orElseThrow());
        qaime.setKontragent(kontragentRepo.findById(kontragentId).orElseThrow());
        qaime.setDuzelisTarixi(LocalDateTime.now());

        // Köhnə sətirləri təmizləyirik
        qaime.getSetirler().clear();

        BigDecimal umumiMebleg = BigDecimal.ZERO;

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

            if ("MAL".equals(novler.get(i)) && mehsulIdler != null && mehsulIdler.size() > i && mehsulIdler.get(i) != null) {
                Mehsul mehsul = mehsulRepo.findById(mehsulIdler.get(i)).orElseThrow();
                setir.setMehsul(mehsul);

                Double kohneMiqdar = mehsul.getMiqdar();
                BigDecimal kohneQaliqDeyeri = mehsul.getQaliqDeyeri() != null ? mehsul.getQaliqDeyeri() : BigDecimal.ZERO;

                Double yeniMiqdar = kohneMiqdar + miqdarlar.get(i).doubleValue();
                BigDecimal yeniQaliqDeyeri = kohneQaliqDeyeri.add(mebleg);
                BigDecimal yeniOrtaDeyer = yeniQaliqDeyeri.divide(BigDecimal.valueOf(yeniMiqdar), 2, RoundingMode.HALF_UP);

                mehsul.setMiqdar(yeniMiqdar);
                mehsul.setQaliqDeyeri(yeniQaliqDeyeri);
                mehsul.setOrtaDeyer(yeniOrtaDeyer);
                mehsulRepo.save(mehsul);
            } else if ("XIDMET".equals(novler.get(i))) {
                setir.setXidmetAdi(xidmetAdlari != null && xidmetAdlari.size() > i ? xidmetAdlari.get(i) : null);
            }

            qaime.getSetirler().add(setir);
        }

        qaime.setUmumiMebleg(umumiMebleg);
        alisQaimesiRepo.save(qaime);
    }

    // Soft Delete (Arxivə köçürmə)
    @Transactional
    public void qaimeSil(Long id) {
        AlisQaimesi qaime = alisQaimesiRepo.findById(id).orElseThrow();
        qaime.setSilinib(true);
        qaime.setSilinmeTarixi(LocalDateTime.now());
        alisQaimesiRepo.save(qaime);
    }
}