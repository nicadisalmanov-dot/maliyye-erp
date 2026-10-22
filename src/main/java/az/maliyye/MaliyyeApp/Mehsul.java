package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;

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

    // Maya dəyəri (İlkin alış qiyməti)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal alisQiymeti;

    // Satış qiyməti
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal satisQiymeti;

    // Anbarda olan miqdar
    @Column(nullable = false)
    private Double miqdar = 0.0;

    // ==========================================
    // YENİ ƏLAVƏ EDİLƏN: ORTA DƏYƏR METODU ÜÇÜN
    // ==========================================

    // Malın cari orta dəyəri (hər alışda yenilənir)
    @Column(precision = 15, scale = 2)
    private BigDecimal ortaDeyer = BigDecimal.ZERO;

    // Anbardaki malın ümumi dəyəri (miqdar * orta dəyər)
    @Column(precision = 15, scale = 2)
    private BigDecimal qaliqDeyeri = BigDecimal.ZERO;

    public Mehsul() {}

    public Mehsul(String ad, MehsulQrupu qrup, BigDecimal alisQiymeti, BigDecimal satisQiymeti, Double miqdar) {
        this.ad = ad;
        this.qrup = qrup;
        this.alisQiymeti = alisQiymeti;
        this.satisQiymeti = satisQiymeti;
        this.miqdar = miqdar;
        // İlk yaradılanda orta dəyər = alış qiyməti
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

    // Setterlər
    public void setId(Long id) { this.id = id; }
    public void setAd(String ad) { this.ad = ad; }
    public void setQrup(MehsulQrupu qrup) { this.qrup = qrup; }
    public void setAlisQiymeti(BigDecimal alisQiymeti) { this.alisQiymeti = alisQiymeti; }
    public void setSatisQiymeti(BigDecimal satisQiymeti) { this.satisQiymeti = satisQiymeti; }
    public void setMiqdar(Double miqdar) { this.miqdar = miqdar; }
    public void setOrtaDeyer(BigDecimal ortaDeyer) { this.ortaDeyer = ortaDeyer; }
    public void setQaliqDeyeri(BigDecimal qaliqDeyeri) { this.qaliqDeyeri = qaliqDeyeri; }
}