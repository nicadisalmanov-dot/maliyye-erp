package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "anbar_mehsullar")
public class AnbarMehsul {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "anbar_id", nullable = false)
    private Anbar anbar;

    @ManyToOne
    @JoinColumn(name = "mehsul_id", nullable = false)
    private Mehsul mehsul;

    @Column(nullable = false)
    private Double miqdar = 0.0;

    @Column(precision = 15, scale = 2)
    private BigDecimal ortaDeyer = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal qaliqDeyeri = BigDecimal.ZERO;

    // YENİ: Bütün anbarların siyahısı (bazada saxlanmır, yalnız göstərmək üçün)
    @Transient
    private List<Anbar> butunAnbarlar;

    public AnbarMehsul() {}

    public AnbarMehsul(Anbar anbar, Mehsul mehsul, Double miqdar, BigDecimal ortaDeyer, BigDecimal qaliqDeyeri) {
        this.anbar = anbar;
        this.mehsul = mehsul;
        this.miqdar = miqdar;
        this.ortaDeyer = ortaDeyer;
        this.qaliqDeyeri = qaliqDeyeri;
    }

    // Getterlər və Setterlər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Anbar getAnbar() { return anbar; }
    public void setAnbar(Anbar anbar) { this.anbar = anbar; }
    public Mehsul getMehsul() { return mehsul; }
    public void setMehsul(Mehsul mehsul) { this.mehsul = mehsul; }
    public Double getMiqdar() { return miqdar; }
    public void setMiqdar(Double miqdar) { this.miqdar = miqdar; }
    public BigDecimal getOrtaDeyer() { return ortaDeyer; }
    public void setOrtaDeyer(BigDecimal ortaDeyer) { this.ortaDeyer = ortaDeyer; }
    public BigDecimal getQaliqDeyeri() { return qaliqDeyeri; }
    public void setQaliqDeyeri(BigDecimal qaliqDeyeri) { this.qaliqDeyeri = qaliqDeyeri; }
    public List<Anbar> getButunAnbarlar() { return butunAnbarlar; }
    public void setButunAnbarlar(List<Anbar> butunAnbarlar) { this.butunAnbarlar = butunAnbarlar; }
}