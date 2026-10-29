package az.maliyye.MaliyyeApp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MaliyyeAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaliyyeAppApplication.class, args);
    }

    // ============================================
    // === ADMIN İSTİFADƏÇİSİNİ YARADIR/YENİLƏYİR ===
    // ============================================
    @Bean
    public CommandLineRunner ilkinAdminYarat(IstifadeciRepository repo, PasswordEncoder encoder) {
        return args -> {
            System.out.println("=== İSTİFADƏÇİ YOXLAMASI BAŞLAYIR ===");

            Istifadeci admin = repo.findByUsername("admin");

            if (admin == null) {
                admin = new Istifadeci();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("1"));
                admin.setAd("Sistem Administratoru");
                admin.setRol("ROLE_ADMIN");
                repo.save(admin);
                System.out.println(">>> YENİ ADMIN YARADILDI! (admin / 1)");
            } else {
                admin.setPassword(encoder.encode("1"));
                admin.setRol("ROLE_ADMIN");
                repo.save(admin);
                System.out.println(">>> MÖVCUD ADMIN TAPILDI, ŞİFRƏSİ VƏ ROLU YENİLƏNDİ! (admin / 1)");
            }
            System.out.println("=== İSTİFADƏÇİ YOXLAMASI BİTDİ ===");
        };
    }

    // ============================================
    // === CONSTRAINT TƏMİZLƏMƏ (YALNIZ LOKAL) ===
    // ============================================
    @Bean
    public CommandLineRunner constraintTemizle(JdbcTemplate jdbcTemplate) {
        return args -> {
            // Yalnız lokal mühitdə işlə (DB_URL yoxdursa)
            String dbUrl = System.getenv("DB_URL");
            if (dbUrl != null && !dbUrl.isEmpty()) {
                System.out.println(">>> BULUD MÜHİTİ: Constraint təmizləmə atlanır.");
                return;
            }

            try {
                // Cədvəlin mövcud olduğunu yoxla
                Integer cedvelSayi = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'alis_qaimeleri_setirleri'",
                        Integer.class);

                if (cedvelSayi == null || cedvelSayi == 0) {
                    System.out.println(">>> CƏDVƏL YOXDUR: Constraint təmizləmə atlanır.");
                    return;
                }

                System.out.println(">>> CONSTRAINT TƏMİZLƏMƏ BAŞLAYIR (LOKAL)...");

                // Bütün constraint-ləri tap
                String sql = "SELECT conname FROM pg_constraint " +
                        "WHERE conrelid = 'alis_qaimeleri_setirleri'::regclass " +
                        "AND confrelid = 'alis_qaimeleri'::regclass";

                java.util.List<String> constraintler = jdbcTemplate.queryForList(sql, String.class);
                System.out.println(">>> TAPILAN CONSTRAINT SAYI: " + constraintler.size());

                // Hər birini sil (CASCADE ilə)
                for (String c : constraintler) {
                    try {
                        jdbcTemplate.execute("ALTER TABLE alis_qaimeleri_setirleri DROP CONSTRAINT " + c + " CASCADE");
                        System.out.println(">>> SİLİNDİ: " + c);
                    } catch (Exception e) {
                        System.out.println(">>> SİLİNMƏDİ: " + c + " - " + e.getMessage());
                    }
                }

                // Yeni, tək bir constraint yarat (CASCADE ilə)
                jdbcTemplate.execute("ALTER TABLE alis_qaimeleri_setirleri " +
                        "ADD CONSTRAINT fk_alis_qaimesi_setri " +
                        "FOREIGN KEY (alis_qaimesi_id) REFERENCES alis_qaimeleri(id) ON DELETE CASCADE");
                System.out.println(">>> YENİ CONSTRAINT YARADILDI (ON DELETE CASCADE)");

                System.out.println(">>> CONSTRAINT TƏMİZLƏMƏ BİTDİ.");
            } catch (Exception e) {
                System.out.println(">>> CONSTRAINT TƏMİZLƏMƏ XƏTASI: " + e.getMessage());
            }
        };
    }

    // ============================================
    // === HR BAYRAMLARINI YÜKLƏ ===
    // ============================================
    @Bean
    public CommandLineRunner bayramlariYukle(BayramService bayramService) {
        return args -> {
            System.out.println("=== BAYRAMLAR YÜKLƏNİR ===");
            try {
                bayramService.ilkinBayramlariYukle();
                System.out.println(">>> BAYRAMLAR UĞURLA YÜKLƏNDİ.");
            } catch (Exception e) {
                System.out.println(">>> BAYRAM YÜKLƏMƏ XƏTASI: " + e.getMessage());
            }
            System.out.println("=== BAYRAMLAR YÜKLƏMƏ BİTDİ ===");
        };
    }
}