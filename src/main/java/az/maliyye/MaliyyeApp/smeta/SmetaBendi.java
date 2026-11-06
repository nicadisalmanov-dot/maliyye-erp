package az.maliyye.MaliyyeApp.smeta;

import jakarta.persistence.*;

@Entity
@Table(name = "smeta_bendleri")
public class SmetaBendi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "smeta_id", nullable = false)
    private Smeta smeta;

    @Column(nullable = false)
    private String isAdi; // Məsələn: "Beton tökülməsi C25/30"

    private String vahid; // m³, m², ton, ədəd

    private Double miqdar = 0.0;

    private Double vahidQiymeti = 0.0;

    // Kateqoriya: MATERIAL, EMAK, TEXNIKA
    private String kateqoriya = "MATERIAL";

    // Cəmi məbləği hesablayan metod
    public Double getCemiMebleg() {
        if (miqdar == null || vahidQiymeti == null) return 0.0;
        return miqdar * vahidQiymeti;
    }

    // Getter və Setter-lər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Smeta getSmeta() { return smeta; }
    public void setSmeta(Smeta smeta) { this.smeta = smeta; }

    public String getIsAdi() { return isAdi; }
    public void setIsAdi(String isAdi) { this.isAdi = isAdi; }

    public String getVahid() { return vahid; }
    public void setVahid(String vahid) { this.vahid = vahid; }

    public Double getMiqdar() { return miqdar; }
    public void setMiqdar(Double miqdar) { this.miqdar = miqdar; }

    public Double getVahidQiymeti() { return vahidQiymeti; }
    public void setVahidQiymeti(Double vahidQiymeti) { this.vahidQiymeti = vahidQiymeti; }

    public String getKateqoriya() { return kateqoriya; }
    public void setKateqoriya(String kateqoriya) { this.kateqoriya = kateqoriya; }
}