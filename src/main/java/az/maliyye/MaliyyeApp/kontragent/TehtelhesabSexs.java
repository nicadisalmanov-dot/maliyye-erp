package az.maliyye.MaliyyeApp.kontragent;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tehtelhesab_sexsler")
public class TehtelhesabSexs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String adSoyad;

    @Column(length = 20)
    private String finKodu;

    @Column(length = 30)
    private String vesiqeNomresi;

    @Column(length = 50)
    private String telefon;

    // YENİ ƏLAVƏ EDİLƏN SAHƏ
    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String unvan;

    @Column(length = 100)
    private String vezife;

    @Column(columnDefinition = "TEXT")
    private String qeydler;

    @Column(updatable = false)
    private LocalDateTime yaradilmaTarixi;

    @Column(updatable = false)
    private String yaradanIstifadeci;

    private LocalDateTime duzelisTarixi;

    private String duzelenIstifadeci;

    @PrePersist
    protected void onCreate() {
        if (this.yaradilmaTarixi == null) {
            this.yaradilmaTarixi = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.duzelisTarixi = LocalDateTime.now();
    }

    public TehtelhesabSexs() {}

    // Getter və Setterlər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAdSoyad() { return adSoyad; }
    public void setAdSoyad(String adSoyad) { this.adSoyad = adSoyad; }

    public String getFinKodu() { return finKodu; }
    public void setFinKodu(String finKodu) { this.finKodu = finKodu; }

    public String getVesiqeNomresi() { return vesiqeNomresi; }
    public void setVesiqeNomresi(String vesiqeNomresi) { this.vesiqeNomresi = vesiqeNomresi; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    // YENİ EMAIL GETTER VƏ SETTER
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUnvan() { return unvan; }
    public void setUnvan(String unvan) { this.unvan = unvan; }

    public String getVezife() { return vezife; }
    public void setVezife(String vezife) { this.vezife = vezife; }

    public String getQeydler() { return qeydler; }
    public void setQeydler(String qeydler) { this.qeydler = qeydler; }

    public LocalDateTime getYaradilmaTarixi() { return yaradilmaTarixi; }
    public void setYaradilmaTarixi(LocalDateTime yaradilmaTarixi) { this.yaradilmaTarixi = yaradilmaTarixi; }

    public String getYaradanIstifadeci() { return yaradanIstifadeci; }
    public void setYaradanIstifadeci(String yaradanIstifadeci) { this.yaradanIstifadeci = yaradanIstifadeci; }

    public LocalDateTime getDuzelisTarixi() { return duzelisTarixi; }
    public void setDuzelisTarixi(LocalDateTime duzelisTarixi) { this.duzelisTarixi = duzelisTarixi; }

    public String getDuzelenIstifadeci() { return duzelenIstifadeci; }
    public void setDuzelenIstifadeci(String duzelenIstifadeci) { this.duzelenIstifadeci = duzelenIstifadeci; }
}