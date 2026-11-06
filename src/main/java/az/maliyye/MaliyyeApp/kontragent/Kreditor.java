package az.maliyye.MaliyyeApp.kontragent;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "kreditorlar")
@Data
public class Kreditor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sirketAdi; // Və ya Təchizatçının adı

    private String subyektNovu; // FIZIKI və ya HUKUQI
    private String voen;
    private String elaqaSexs;
    private String telefon;
    private String email;
    private String unvan;

    private String yaradanIstifadeci;
    private LocalDateTime yaradilmaTarixi = LocalDateTime.now();
}