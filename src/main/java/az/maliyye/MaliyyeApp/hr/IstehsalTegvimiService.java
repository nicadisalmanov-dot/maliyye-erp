package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class IstehsalTegvimiService {

    @Autowired private IstehsalTegvimiRepository teqvimRepo;
    @Autowired private BayramRepository bayramRepo;

    // Müəyyən bir ay üçün təqvimi yaradır (əgər yoxdursa)
    @Transactional
    public void ayinTegviminiYarat(int il, int ay) {
        LocalDate baslangic = LocalDate.of(il, ay, 1);
        LocalDate son = baslangic.withDayOfMonth(baslangic.lengthOfMonth());

        // Əgər bu ay üçün artıq təqvim varsa, yenidən yaratmırıq
        if (!teqvimRepo.findByTarixBetween(baslangic, son).isEmpty()) {
            return;
        }

        // Ayın bütün günlərini yaradırıq
        for (LocalDate d = baslangic; !d.isAfter(son); d = d.plusDays(1)) {
            String gunNovu = "ISH_GUNU";
            String qeyd = null;

            // XƏTANI HƏLL EDƏN HİSSƏ:
            // Lambda daxilində istifadə üçün dəyişməz (final) dəyişən yaradırıq
            final LocalDate cedvelTarixi = d;

            // 1. Həftəsonu yoxlaması
            if (cedvelTarixi.getDayOfWeek() == DayOfWeek.SATURDAY || cedvelTarixi.getDayOfWeek() == DayOfWeek.SUNDAY) {
                gunNovu = "HEFTE_SONU";
            }

            // 2. Bayram yoxlaması (BayramRepository-dən)
            Optional<Bayram> bayram = bayramRepo.findAll().stream()
                    .filter(b -> b.getTarix().equals(cedvelTarixi))
                    .findFirst();
            if (bayram.isPresent()) {
                gunNovu = "BAYRAM";
                qeyd = bayram.get().getAd();
            }

            teqvimRepo.save(new IstehsalTegvimi(d, gunNovu, qeyd));
        }
    }

    // Müəyyən bir gün üçün statusu qaytarır (Tabel üçün)
    public String gunNovunuAl(LocalDate tarix) {
        Optional<IstehsalTegvimi> teqvim = teqvimRepo.findByTarix(tarix);
        if (teqvim.isPresent()) {
            return teqvim.get().getGunNovu();
        }
        // Əgər təqvimdə yoxdursa, default olaraq həftəsonu/İş günü yoxlayırıq
        if (tarix.getDayOfWeek() == DayOfWeek.SATURDAY || tarix.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return "HEFTE_SONU";
        }
        return "ISH_GUNU";
    }

    // İstifadəçi tərəfindən günün statusunu dəyişmək üçün
    @Transactional
    public void gunStatusunuDeyis(LocalDate tarix, String yeniStatus, String qeyd) {
        IstehsalTegvimi teqvim = teqvimRepo.findByTarix(tarix)
                .orElseGet(() -> new IstehsalTegvimi(tarix, "ISH_GUNU", null));
        teqvim.setGunNovu(yeniStatus);
        teqvim.setQeyd(qeyd);
        teqvimRepo.save(teqvim);
    }

    // Bir ay üçün bütün təqvimi qaytarır (Frontend cədvəli üçün)
    public List<IstehsalTegvimi> ayinTegvimi(int il, int ay) {
        LocalDate baslangic = LocalDate.of(il, ay, 1);
        LocalDate son = baslangic.withDayOfMonth(baslangic.lengthOfMonth());
        // Əvvəlcə təqvimi yaradırıq (əgər yoxdursa)
        ayinTegviminiYarat(il, ay);
        return teqvimRepo.findByTarixBetween(baslangic, son);
    }
}