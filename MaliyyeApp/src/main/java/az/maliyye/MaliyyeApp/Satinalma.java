package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "satinalmalar")
public class Satinalma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate tarix;

    // Hansı təchizatçıdan alındı
    @ManyToOne
    @JoinColumn(name = "kontragent_id", nullable = false)
    private Kontragent kontragent;

    // Hansı məhsul alındı
    @ManyToOne
    @JoinColumn(name = "mehsul_id", nullable = false)
    private Mehsul mehsul;

    @Column(nullable = false)
    private Double miqdar;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal alisQiymeti; // Təchizatçıya ödənilən qiymət

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal umumiMebleg;

    public Satinalma() {}

    public Satinalma(LocalDate tarix, Kontragent kontragent, Mehsul mehsul, Double miqdar, BigDecimal alisQiymeti, BigDecimal umumiMebleg) {
        this.tarix = tarix;
        this.kontragent = kontragent;
        this.mehsul = mehsul;
        this.miqdar = miqdar;
        this.alisQiymeti = alisQiymeti;
        this.umumiMebleg = umumiMebleg;
    }

    public Long getId() { return id; }
    public LocalDate getTarix() { return tarix; }
    public Kontragent getKontragent() { return kontragent; }
    public Mehsul getMehsul() { return mehsul; }
    public Double getMiqdar() { return miqdar; }
    public BigDecimal getAlisQiymeti() { return alisQiymeti; }
    public BigDecimal getUmumiMebleg() { return umumiMebleg; }

    public void setId(Long id) { this.id = id; }
    public void setTarix(LocalDate tarix) { this.tarix = tarix; }
    public void setKontragent(Kontragent kontragent) { this.kontragent = kontragent; }
    public void setMehsul(Mehsul mehsul) { this.mehsul = mehsul; }
    public void setMiqdar(Double miqdar) { this.miqdar = miqdar; }
    public void setAlisQiymeti(BigDecimal alisQiymeti) { this.alisQiymeti = alisQiymeti; }
    public void setUmumiMebleg(BigDecimal umumiMebleg) { this.umumiMebleg = umumiMebleg; }
}