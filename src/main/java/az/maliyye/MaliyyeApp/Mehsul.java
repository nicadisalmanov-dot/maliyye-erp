package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "mehsullar")
public class Mehsul {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String ad;

    // Məhsulun qrupu (Bazada qrupun ID-si saxlanılır)
    @ManyToOne
    @JoinColumn(name = "qrup_id", nullable = false)
    private MehsulQrupu qrup;

    // Satış qiyməti
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal satisQiymeti;

    // Anbarda olan miqdar (Hələlik sadə rəqəm kimi saxlayırıq)
    @Column(nullable = false)
    private Double miqdar = 0.0;

    public Mehsul() {}

    public Mehsul(String ad, MehsulQrupu qrup, BigDecimal satisQiymeti, Double miqdar) {
        this.ad = ad;
        this.qrup = qrup;
        this.satisQiymeti = satisQiymeti;
        this.miqdar = miqdar;
    }

    public Long getId() { return id; }
    public String getAd() { return ad; }
    public MehsulQrupu getQrup() { return qrup; }
    public BigDecimal getSatisQiymeti() { return satisQiymeti; }
    public Double getMiqdar() { return miqdar; }

    public void setId(Long id) { this.id = id; }
    public void setAd(String ad) { this.ad = ad; }
    public void setQrup(MehsulQrupu qrup) { this.qrup = qrup; }
    public void setSatisQiymeti(BigDecimal satisQiymeti) { this.satisQiymeti = satisQiymeti; }
    public void setMiqdar(Double miqdar) { this.miqdar = miqdar; }
}