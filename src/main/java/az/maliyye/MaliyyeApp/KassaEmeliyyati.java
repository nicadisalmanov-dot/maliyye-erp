package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kassa_emeliyyatlar")
public class KassaEmeliyyati {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String senedNomresi; // K-00001

    @Column(nullable = false)
    private LocalDate tarix;

    @ManyToOne
    @JoinColumn(name = "layihe_id")
    private Layihe layihe; // Layihə adı

    @ManyToOne
    @JoinColumn(name = "kassa_hesabi_id")
    private KassaHesabi kassaHesabi; // Kassa/Bank/ƏDV Depozit

    @Column(length = 50)
    private String hesablasmaHesabi; // Hesablaşma hesabı (Kontragent, Borc müqaviləsi, Təhtəlhesab şəxs)

    @ManyToOne
    @JoinColumn(name = "kontragent_id")
    private Kontragent kontragent; // Kontragent

    @Column(length = 300)
    private String tesvir; // Açıqlama

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal medaxil = BigDecimal.ZERO; // Mədaxil

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal mexaric = BigDecimal.ZERO; // Məxaric

    @Column(length = 200)
    private String xercMaddesi; // Xərc maddəsi

    // --- Audit (İzləmə) Sahələri ---
    @Column(name = "yaradilma_tarixi")
    private LocalDateTime yaradilmaTarixi;

    @Column(name = "duzelis_tarixi")
    private LocalDateTime duzelisTarixi;

    @ManyToOne
    @JoinColumn(name = "yaradan_istifadeci_id")
    private Istifadeci yaradanIstifadeci;

    @Column(name = "tesdiqlenib")
    private Boolean tesdiqlenib = false; // Mərkəz tərəfindən təsdiqlənib

    @ManyToOne
    @JoinColumn(name = "tesdiqleyen_istifadeci_id")
    private Istifadeci tesdiqleyenIstifadeci;

    @Column(name = "tesdiq_tarixi")
    private LocalDateTime tesdiqTarixi;

    public KassaEmeliyyati() {
        this.yaradilmaTarixi = LocalDateTime.now();
        this.tesdiqlenib = false;
    }

    // Getterlər və Setterlər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSenedNomresi() { return senedNomresi; }
    public void setSenedNomresi(String senedNomresi) { this.senedNomresi = senedNomresi; }

    public LocalDate getTarix() { return tarix; }
    public void setTarix(LocalDate tarix) { this.tarix = tarix; }

    public Layihe getLayihe() { return layihe; }
    public void setLayihe(Layihe layihe) { this.layihe = layihe; }

    public KassaHesabi getKassaHesabi() { return kassaHesabi; }
    public void setKassaHesabi(KassaHesabi kassaHesabi) { this.kassaHesabi = kassaHesabi; }

    public String getHesablasmaHesabi() { return hesablasmaHesabi; }
    public void setHesablasmaHesabi(String hesablasmaHesabi) { this.hesablasmaHesabi = hesablasmaHesabi; }

    public Kontragent getKontragent() { return kontragent; }
    public void setKontragent(Kontragent kontragent) { this.kontragent = kontragent; }

    public String getTesvir() { return tesvir; }
    public void setTesvir(String tesvir) { this.tesvir = tesvir; }

    public BigDecimal getMedaxil() { return medaxil; }
    public void setMedaxil(BigDecimal medaxil) { this.medaxil = medaxil; }

    public BigDecimal getMexaric() { return mexaric; }
    public void setMexaric(BigDecimal mexaric) { this.mexaric = mexaric; }

    public String getXercMaddesi() { return xercMaddesi; }
    public void setXercMaddesi(String xercMaddesi) { this.xercMaddesi = xercMaddesi; }

    public LocalDateTime getYaradilmaTarixi() { return yaradilmaTarixi; }
    public void setYaradilmaTarixi(LocalDateTime yaradilmaTarixi) { this.yaradilmaTarixi = yaradilmaTarixi; }

    public LocalDateTime getDuzelisTarixi() { return duzelisTarixi; }
    public void setDuzelisTarixi(LocalDateTime duzelisTarixi) { this.duzelisTarixi = duzelisTarixi; }

    public Istifadeci getYaradanIstifadeci() { return yaradanIstifadeci; }
    public void setYaradanIstifadeci(Istifadeci yaradanIstifadeci) { this.yaradanIstifadeci = yaradanIstifadeci; }

    public Boolean getTesdiqlenib() { return tesdiqlenib; }
    public void setTesdiqlenib(Boolean tesdiqlenib) { this.tesdiqlenib = tesdiqlenib; }

    public Istifadeci getTesdiqleyenIstifadeci() { return tesdiqleyenIstifadeci; }
    public void setTesdiqleyenIstifadeci(Istifadeci tesdiqleyenIstifadeci) { this.tesdiqleyenIstifadeci = tesdiqleyenIstifadeci; }

    public LocalDateTime getTesdiqTarixi() { return tesdiqTarixi; }
    public void setTesdiqTarixi(LocalDateTime tesdiqTarixi) { this.tesdiqTarixi = tesdiqTarixi; }

    public boolean isMedaxilVar() { return medaxil != null && medaxil.compareTo(BigDecimal.ZERO) > 0; }
    public boolean isMexaricVar() { return mexaric != null && mexaric.compareTo(BigDecimal.ZERO) > 0; }
}