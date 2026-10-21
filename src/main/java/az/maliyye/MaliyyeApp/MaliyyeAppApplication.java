package az.maliyye.MaliyyeApp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MaliyyeAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaliyyeAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner ilkinAdminYarat(IstifadeciRepository repo, PasswordEncoder encoder) {
        return args -> {
            System.out.println("=== İSTİFADƏÇİ YOXLAMASI BAŞLAYIR ===");

            Istifadeci admin = repo.findByUsername("admin");

            if (admin == null) {
                // Əgər yoxdursa, yeni yaradırıq
                admin = new Istifadeci();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setAd("Sistem Administratoru");
                admin.setRol("ROLE_ADMIN"); // BURADA ROLE_ əlavə etdik
                repo.save(admin);
                System.out.println(">>> YENİ ADMIN YARADILDI! (admin / admin123)");
            } else {
                // Əgər varsa, şifrəsini və rolunu yeniləyirik
                admin.setPassword(encoder.encode("admin123"));
                admin.setRol("ROLE_ADMIN"); // KÖHNƏ ADMINİN ROLUNU DA YENİLƏYİRİK
                repo.save(admin);
                System.out.println(">>> MÖVCUD ADMIN TAPILDI, ŞİFRƏSİ VƏ ROLU YENİLƏNDİ! (admin / admin123)");
            }
            System.out.println("=== İSTİFADƏÇİ YOXLAMASI BİTDİ ===");
        };
    }
}