package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mehsullar")
public class Mehsul {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String ad;

    @ManyToOne
    @JoinColumn(name = "qrup_id", nullable = false)
    private MehsulQrupu qrup;

    // YENİ: Mal tipi əlaqəsi
    @ManyToOne
    @JoinColumn(name = "mal_tipi_id")
    private MalTipi malTipi;

    @Column(length = 20)
    private String vahid = "əd";

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal alisQiymeti;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal satisQiymeti;

    @ManyToOne
    @JoinColumn(name = "anbar_id")
    private Anbar anbar;

    @Column(precision = 15, scale = 2)
    private BigDecimal ortaDeyer = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal qaliqDeyeri = BigDecimal.ZERO;

    private Double miqdar = 0.0;

    @Column(name = "yaradilma_tarixi")
    private LocalDateTime yaradilmaTarixi;

    @Column(name = "duzelis_tarixi")
    private LocalDateTime duzelisTarixi;

    @ManyToOne
    @JoinColumn(name = "yaradan_istifadeci_id")
    private Istifadeci yaradanIstifadeci;

    public Mehsul() {
        this.yaradilmaTarixi = LocalDateTime.now();
    }

    public Mehsul(String ad, MehsulQrupu qrup, MalTipi malTipi, BigDecimal alisQiymeti, BigDecimal satisQiymeti, Double miqdar) {
        this.ad = ad;
        this.qrup = qrup;
        this.malTipi = malTipi;
        this.alisQiymeti = alisQiymeti;
        this.satisQiymeti = satisQiymeti;
        this.miqdar = miqdar;
        this.yaradilmaTarixi = LocalDateTime.now();
        this.ortaDeyer = alisQiymeti;
    }

    // Getterlər və Setterlər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public MehsulQrupu getQrup() { return qrup; }
    public void setQrup(MehsulQrupu qrup) { this.qrup = qrup; }

    public MalTipi getMalTipi() { return malTipi; }
    public void setMalTipi(MalTipi malTipi) { this.malTipi = malTipi; }

    public String getVahid() { return vahid; }
    public void setVahid(String vahid) { this.vahid = vahid; }

    public BigDecimal getAlisQiymeti() { return alisQiymeti; }
    public void setAlisQiymeti(BigDecimal alisQiymeti) { this.alisQiymeti = alisQiymeti; }

    public BigDecimal getSatisQiymeti() { return satisQiymeti; }
    public void setSatisQiymeti(BigDecimal satisQiymeti) { this.satisQiymeti = satisQiymeti; }

    public Anbar getAnbar() { return anbar; }
    public void setAnbar(Anbar anbar) { this.anbar = anbar; }

    public BigDecimal getOrtaDeyer() { return ortaDeyer; }
    public void setOrtaDeyer(BigDecimal ortaDeyer) { this.ortaDeyer = ortaDeyer; }

    public BigDecimal getQaliqDeyeri() { return qaliqDeyeri; }
    public void setQaliqDeyeri(BigDecimal qaliqDeyeri) { this.qaliqDeyeri = qaliqDeyeri; }

    public Double getMiqdar() { return miqdar; }
    public void setMiqdar(Double miqdar) { this.miqdar = miqdar; }

    public LocalDateTime getYaradilmaTarixi() { return yaradilmaTarixi; }
    public void setYaradilmaTarixi(LocalDateTime yaradilmaTarixi) { this.yaradilmaTarixi = yaradilmaTarixi; }

    public LocalDateTime getDuzelisTarixi() { return duzelisTarixi; }
    public void setDuzelisTarixi(LocalDateTime duzelisTarixi) { this.duzelisTarixi = duzelisTarixi; }

    public Istifadeci getYaradanIstifadeci() { return yaradanIstifadeci; }
    public void setYaradanIstifadeci(Istifadeci yaradanIstifadeci) { this.yaradanIstifadeci = yaradanIstifadeci; }
}