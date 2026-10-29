package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "isciler")
public class Isci {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Şəxsi məlumatlar
    @Column(nullable = false, length = 50)
    private String ad;

    @Column(nullable = false, length = 50)
    private String soyad;

    @Column(length = 50)
    private String ataAdi;

    @Column(length = 20)
    private String finKod;

    private LocalDate dogumTarixi;

    @Column(length = 20)
    private String cins;

    @Column(length = 20)
    private String telefon;

    @Column(length = 100)
    private String email;

    @Column(length = 300)
    private String unvan;

    // İş məlumatları
    @ManyToOne
    @JoinColumn(name = "sobe_id")
    private Sobe sobe;

    @ManyToOne
    @JoinColumn(name = "vezife_id")
    private Vezife vezife;

    private LocalDate iseQebulTarixi;

    @Column(precision = 15, scale = 2)
    private BigDecimal emekHaqqi = BigDecimal.ZERO;

    @Column(length = 30)
    private String status = "AKTIV"; // AKTIV, MEZUNIYYETDE, ISDEN_CIXIB

    // Əlaqə şəxsi
    @Column(length = 100)
    private String teciliElaqeAd;

    @Column(length = 20)
    private String teciliElaqeTelefon;

    // Audit
    @Column(name = "yaradilma_tarixi")
    private LocalDateTime yaradilmaTarixi;

    @ManyToOne
    @JoinColumn(name = "yaradan_istifadeci_id")
    private Istifadeci yaradanIstifadeci;

    public Isci() {
        this.yaradilmaTarixi = LocalDateTime.now();
    }

    public String getTamAd() {
        return ad + " " + soyad + (ataAdi != null ? " " + ataAdi : "");
    }

    // Getterlər və Setterlər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public String getSoyad() { return soyad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }
    public String getAtaAdi() { return ataAdi; }
    public void setAtaAdi(String ataAdi) { this.ataAdi = ataAdi; }
    public String getFinKod() { return finKod; }
    public void setFinKod(String finKod) { this.finKod = finKod; }
    public LocalDate getDogumTarixi() { return dogumTarixi; }
    public void setDogumTarixi(LocalDate dogumTarixi) { this.dogumTarixi = dogumTarixi; }
    public String getCins() { return cins; }
    public void setCins(String cins) { this.cins = cins; }
    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUnvan() { return unvan; }
    public void setUnvan(String unvan) { this.unvan = unvan; }
    public Sobe getSobe() { return sobe; }
    public void setSobe(Sobe sobe) { this.sobe = sobe; }
    public Vezife getVezife() { return vezife; }
    public void setVezife(Vezife vezife) { this.vezife = vezife; }
    public LocalDate getIseQebulTarixi() { return iseQebulTarixi; }
    public void setIseQebulTarixi(LocalDate iseQebulTarixi) { this.iseQebulTarixi = iseQebulTarixi; }
    public BigDecimal getEmekHaqqi() { return emekHaqqi; }
    public void setEmekHaqqi(BigDecimal emekHaqqi) { this.emekHaqqi = emekHaqqi; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTeciliElaqeAd() { return teciliElaqeAd; }
    public void setTeciliElaqeAd(String teciliElaqeAd) { this.teciliElaqeAd = teciliElaqeAd; }
    public String getTeciliElaqeTelefon() { return teciliElaqeTelefon; }
    public void setTeciliElaqeTelefon(String teciliElaqeTelefon) { this.teciliElaqeTelefon = teciliElaqeTelefon; }
    public LocalDateTime getYaradilmaTarixi() { return yaradilmaTarixi; }
    public void setYaradilmaTarixi(LocalDateTime yaradilmaTarixi) { this.yaradilmaTarixi = yaradilmaTarixi; }
    public Istifadeci getYaradanIstifadeci() { return yaradanIstifadeci; }
    public void setYaradanIstifadeci(Istifadeci yaradanIstifadeci) { this.yaradanIstifadeci = yaradanIstifadeci; }
}