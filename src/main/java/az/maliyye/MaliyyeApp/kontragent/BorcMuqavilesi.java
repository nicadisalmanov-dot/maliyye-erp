package az.maliyye.MaliyyeApp.kontragent;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "borc_muqavileleri")
public class BorcMuqavilesi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subyektNovu;

    @Column(nullable = false)
    private String borcNovu;

    private String muqavileNomresi;

    private String adSoyad;
    private String finKodu;
    private String vesiqeNomresi;

    // DÜZƏLİŞ: sirkətAdi -> sirketAdi (ə hərfi ilə Java dəyişəni yazılmır)
    private String sirketAdi;
    private String voen;

    private String telefon;
    private String unvan;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal mebleq;

    private LocalDate baglanmaTarixi;
    private LocalDate bitmeTarixi;

    @Column(length = 1000)
    private String qeydler;

    private String yaradanIstifadeci;
    private LocalDateTime yaradilmaTarixi;
    private String duzelenIstifadeci;
    private LocalDateTime duzelisTarixi;

    public BorcMuqavilesi() {
        this.yaradilmaTarixi = LocalDateTime.now();
    }

    // Getter və Setter-lər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSubyektNovu() { return subyektNovu; }
    public void setSubyektNovu(String subyektNovu) { this.subyektNovu = subyektNovu; }

    public String getBorcNovu() { return borcNovu; }
    public void setBorcNovu(String borcNovu) { this.borcNovu = borcNovu; }

    public String getMuqavileNomresi() { return muqavileNomresi; }
    public void setMuqavileNomresi(String muqavileNomresi) { this.muqavileNomresi = muqavileNomresi; }

    public String getAdSoyad() { return adSoyad; }
    public void setAdSoyad(String adSoyad) { this.adSoyad = adSoyad; }

    public String getFinKodu() { return finKodu; }
    public void setFinKodu(String finKodu) { this.finKodu = finKodu; }

    public String getVesiqeNomresi() { return vesiqeNomresi; }
    public void setVesiqeNomresi(String vesiqeNomresi) { this.vesiqeNomresi = vesiqeNomresi; }

    // DÜZƏLİŞ: getSirketAdi və setSirketAdi (ə hərfi olmadan)
    public String getSirketAdi() { return sirketAdi; }
    public void setSirketAdi(String sirketAdi) { this.sirketAdi = sirketAdi; }

    public String getVoen() { return voen; }
    public void setVoen(String voen) { this.voen = voen; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getUnvan() { return unvan; }
    public void setUnvan(String unvan) { this.unvan = unvan; }

    public BigDecimal getMebleq() { return mebleq; }
    public void setMebleq(BigDecimal mebleq) { this.mebleq = mebleq; }

    public LocalDate getBaglanmaTarixi() { return baglanmaTarixi; }
    public void setBaglanmaTarixi(LocalDate baglanmaTarixi) { this.baglanmaTarixi = baglanmaTarixi; }

    public LocalDate getBitmeTarixi() { return bitmeTarixi; }
    public void setBitmeTarixi(LocalDate bitmeTarixi) { this.bitmeTarixi = bitmeTarixi; }

    public String getQeydler() { return qeydler; }
    public void setQeydler(String qeydler) { this.qeydler = qeydler; }

    public String getYaradanIstifadeci() { return yaradanIstifadeci; }
    public void setYaradanIstifadeci(String yaradanIstifadeci) { this.yaradanIstifadeci = yaradanIstifadeci; }

    public LocalDateTime getYaradilmaTarixi() { return yaradilmaTarixi; }
    public void setYaradilmaTarixi(LocalDateTime yaradilmaTarixi) { this.yaradilmaTarixi = yaradilmaTarixi; }

    public String getDuzelenIstifadeci() { return duzelenIstifadeci; }
    public void setDuzelenIstifadeci(String duzelenIstifadeci) { this.duzelenIstifadeci = duzelenIstifadeci; }

    public LocalDateTime getDuzelisTarixi() { return duzelisTarixi; }
    public void setDuzelisTarixi(LocalDateTime duzelisTarixi) { this.duzelisTarixi = duzelisTarixi; }
}