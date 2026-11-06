package az.maliyye.MaliyyeApp.kontragent;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "kontragentler")
public class Kontragent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "FIZIKI" (Vətəndaş) və ya "HUKUQI" (Şirkət)
    @Column(nullable = false)
    private String subyektNovu;

    // "MUSTERI" (Debitor) və ya "TECHIZATCI" (Kreditor)
    @Column(nullable = false)
    private String kontragentNovu;

    // --- Fiziki şəxs üçün sahələr ---
    private String adSoyad;
    private String finKodu;
    private String vesiqeNomresi;

    // --- Hüquqi şəxs üçün sahələr ---
    private String sirketAdi;
    private String voen;

    // --- Ortaq sahələr ---
    private String telefon;
    private String email;
    private String unvan;

    // --- Audit sahələri ---
    private String yaradanIstifadeci;
    private LocalDateTime yaradilmaTarixi;
    private String duzelenIstifadeci;
    private LocalDateTime duzelisTarixi;

    public Kontragent() {
        this.yaradilmaTarixi = LocalDateTime.now();
    }

    // Hesabat və digər kontrollerlərin xəta verməməsi üçün köməkçi metod:
    public String getAd() {
        if ("FIZIKI".equals(subyektNovu)) {
            return adSoyad != null ? adSoyad : "-";
        } else {
            return sirketAdi != null ? sirketAdi : "-";
        }
    }

    // Getter və Setter-lər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSubyektNovu() { return subyektNovu; }
    public void setSubyektNovu(String subyektNovu) { this.subyektNovu = subyektNovu; }

    public String getKontragentNovu() { return kontragentNovu; }
    public void setKontragentNovu(String kontragentNovu) { this.kontragentNovu = kontragentNovu; }

    public String getAdSoyad() { return adSoyad; }
    public void setAdSoyad(String adSoyad) { this.adSoyad = adSoyad; }

    public String getFinKodu() { return finKodu; }
    public void setFinKodu(String finKodu) { this.finKodu = finKodu; }

    public String getVesiqeNomresi() { return vesiqeNomresi; }
    public void setVesiqeNomresi(String vesiqeNomresi) { this.vesiqeNomresi = vesiqeNomresi; }

    public String getSirketAdi() { return sirketAdi; }
    public void setSirketAdi(String sirketAdi) { this.sirketAdi = sirketAdi; }

    public String getVoen() { return voen; }
    public void setVoen(String voen) { this.voen = voen; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUnvan() { return unvan; }
    public void setUnvan(String unvan) { this.unvan = unvan; }

    public String getYaradanIstifadeci() { return yaradanIstifadeci; }
    public void setYaradanIstifadeci(String yaradanIstifadeci) { this.yaradanIstifadeci = yaradanIstifadeci; }

    public LocalDateTime getYaradilmaTarixi() { return yaradilmaTarixi; }
    public void setYaradilmaTarixi(LocalDateTime yaradilmaTarixi) { this.yaradilmaTarixi = yaradilmaTarixi; }

    public String getDuzelenIstifadeci() { return duzelenIstifadeci; }
    public void setDuzelenIstifadeci(String duzelenIstifadeci) { this.duzelenIstifadeci = duzelenIstifadeci; }

    public LocalDateTime getDuzelisTarixi() { return duzelisTarixi; }
    public void setDuzelisTarixi(LocalDateTime duzelisTarixi) { this.duzelisTarixi = duzelisTarixi; }
}