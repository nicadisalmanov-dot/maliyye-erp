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

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal alisQiymeti;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal satisQiymeti;

    @Column(nullable = false)
    private Double miqdar = 0.0;

    // Orta Dəyər Metodu üçün
    @Column(precision = 15, scale = 2)
    private BigDecimal ortaDeyer = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal qaliqDeyeri = BigDecimal.ZERO;

    // --- YENİ: Audit (İzləmə) Sahələri ---
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

    public Mehsul(String ad, MehsulQrupu qrup, BigDecimal alisQiymeti, BigDecimal satisQiymeti, Double miqdar) {
        this.ad = ad;
        this.qrup = qrup;
        this.alisQiymeti = alisQiymeti;
        this.satisQiymeti = satisQiymeti;
        this.miqdar = miqdar;
        this.yaradilmaTarixi = LocalDateTime.now(); // YENİ
        this.ortaDeyer = alisQiymeti;
        this.qaliqDeyeri = alisQiymeti.multiply(BigDecimal.valueOf(miqdar));
    }

    // Getterlər
    public Long getId() { return id; }
    public String getAd() { return ad; }
    public MehsulQrupu getQrup() { return qrup; }
    public BigDecimal getAlisQiymeti() { return alisQiymeti; }
    public BigDecimal getSatisQiymeti() { return satisQiymeti; }
    public Double getMiqdar() { return miqdar; }
    public BigDecimal getOrtaDeyer() { return ortaDeyer; }
    public BigDecimal getQaliqDeyeri() { return qaliqDeyeri; }
    public LocalDateTime getYaradilmaTarixi() { return yaradilmaTarixi; }
    public LocalDateTime getDuzelisTarixi() { return duzelisTarixi; }
    public Istifadeci getYaradanIstifadeci() { return yaradanIstifadeci; }

    // Setterlər
    public void setId(Long id) { this.id = id; }
    public void setAd(String ad) { this.ad = ad; }
    public void setQrup(MehsulQrupu qrup) { this.qrup = qrup; }
    public void setAlisQiymeti(BigDecimal alisQiymeti) { this.alisQiymeti = alisQiymeti; }
    public void setSatisQiymeti(BigDecimal satisQiymeti) { this.satisQiymeti = satisQiymeti; }
    public void setMiqdar(Double miqdar) { this.miqdar = miqdar; }
    public void setOrtaDeyer(BigDecimal ortaDeyer) { this.ortaDeyer = ortaDeyer; }
    public void setQaliqDeyeri(BigDecimal qaliqDeyeri) { this.qaliqDeyeri = qaliqDeyeri; }
    public void setYaradilmaTarixi(LocalDateTime yaradilmaTarixi) { this.yaradilmaTarixi = yaradilmaTarixi; }
    public void setDuzelisTarixi(LocalDateTime duzelisTarixi) { this.duzelisTarixi = duzelisTarixi; }
    public void setYaradanIstifadeci(Istifadeci yaradanIstifadeci) { this.yaradanIstifadeci = yaradanIstifadeci; }
}