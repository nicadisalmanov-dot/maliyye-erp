package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BayramService {

    @Autowired private BayramRepository bayramRepo;

    // Azərbaycanın sabit bayramlarını yüklə (bir dəfə işə düşəndə)
    public void ilkinBayramlariYukle() {
        int cariIl = LocalDate.now().getYear();
        for (int il = cariIl - 1; il <= cariIl + 1; il++) {
            // Yanvar 1-2 — Yeni il
            bayramElaveEt("Yeni il", LocalDate.of(il, 1, 1), "RESMI");
            bayramElaveEt("Yeni il (2-ci gün)", LocalDate.of(il, 1, 2), "RESMI");
            // Yanvar 20 — Qara Yanvar
            bayramElaveEt("20 Yanvar", LocalDate.of(il, 1, 20), "RESMI");
            // Mart 8 — Qadınlar günü
            bayramElaveEt("Qadınlar günü", LocalDate.of(il, 3, 8), "RESMI");
            // Mart 20-21 — Novruz bayramı
            bayramElaveEt("Novruz bayramı (1-ci gün)", LocalDate.of(il, 3, 20), "RESMI");
            bayramElaveEt("Novruz bayramı (2-ci gün)", LocalDate.of(il, 3, 21), "RESMI");
            // May 9 — Faşizm üzərində qələbə
            bayramElaveEt("Faşizm üzərində qələbə", LocalDate.of(il, 5, 9), "RESMI");
            // May 28 — Müstəqillik günü
            bayramElaveEt("Müstəqillik günü", LocalDate.of(il, 5, 28), "RESMI");
            // İyun 15 — Milli Qurtuluş günü
            bayramElaveEt("Milli Qurtuluş günü", LocalDate.of(il, 6, 15), "RESMI");
            // İyun 26 — Azərbaycan Silahlı Qüvvələri günü
            bayramElaveEt("Silahlı Qüvvələr günü", LocalDate.of(il, 6, 26), "RESMI");
            // Noyabr 8 — Zəfər günü
            bayramElaveEt("Zəfər günü", LocalDate.of(il, 11, 8), "RESMI");
            // Noyabr 9 — Dövlət Bayrağı günü
            bayramElaveEt("Dövlət Bayrağı günü", LocalDate.of(il, 11, 9), "RESMI");
            // Noyabr 12 — Konstitusiya günü
            bayramElaveEt("Konstitusiya günü", LocalDate.of(il, 11, 12), "RESMI");
            // Noyabr 17 — Milli Dirçəliş günü
            bayramElaveEt("Milli Dirçəliş günü", LocalDate.of(il, 11, 17), "RESMI");
            // Dekabr 31 — Dünya Azərbaycanlılarının Həmrəyliyi günü
            bayramElaveEt("Həmrəyliyi günü", LocalDate.of(il, 12, 31), "RESMI");
        }
    }

    private void bayramElaveEt(String ad, LocalDate tarix, String nov) {
        if (!bayramRepo.existsByTarix(tarix)) {
            Bayram b = new Bayram(ad, tarix);
            b.setNov(nov);
            bayramRepo.save(b);
        }
    }

    public boolean isBayram(LocalDate tarix) {
        return bayramRepo.existsByTarix(tarix);
    }
}