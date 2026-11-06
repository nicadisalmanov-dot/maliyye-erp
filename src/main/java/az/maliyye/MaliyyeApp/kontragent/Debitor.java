package az.maliyye.MaliyyeApp.kontragent;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "debitorlar")
@Data
public class Debitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String adSoyad; // Və ya Şirkət adı

    private String subyektNovu; // FIZIKI və ya HUKUQI
    private String finKodu;
    private String vesiqeNomresi;
    private String voen;
    private String telefon;
    private String email;
    private String unvan;

    private String yaradanIstifadeci;
    private LocalDateTime yaradilmaTarixi = LocalDateTime.now();
}