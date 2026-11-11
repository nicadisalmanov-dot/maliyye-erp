package az.maliyye.MaliyyeApp.smeta;

import jakarta.persistence.*;

@Entity
@Table(name = "smeta_bendleri")
public class SmetaBendi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // YENİ: Büdcə kodu (Məs: A.1.1)
    private String kod;

    @ManyToOne
    @JoinColumn(name = "smeta_id", nullable = false)
    private Smeta smeta;

    @ManyToOne
    @JoinColumn(name = "alt_basliq_id", nullable = true)
    private SmetaAltBasliq altBasliq;

    @Column(nullable = false)
    private String isAdi;

    private String vahid;
    private Double miqdar = 0.0;
    private Double vahidQiymeti = 0.0;
    private String kateqoriya = "MATERIAL";

    public Double getCemiMebleg() {
        if (miqdar == null || vahidQiymeti == null) return 0.0;
        return miqdar * vahidQiymeti;
    }

    @Transient
    private Double faktikiXerc = 0.0;

    public Double getFaktikiXerc() { return faktikiXerc != null ? faktikiXerc : 0.0; }
    public void setFaktikiXerc(Double faktikiXerc) { this.faktikiXerc = faktikiXerc; }

    public Double getQaliqMebleg() { return getCemiMebleg() - getFaktikiXerc(); }

    public Double getIcraFaizi() {
        if (getCemiMebleg() == null || getCemiMebleg() == 0.0) return 0.0;
        return Math.min(100.0, (getFaktikiXerc() / getCemiMebleg()) * 100.0);
    }

    // Getter və Setter-lər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKod() { return kod; }
    public void setKod(String kod) { this.kod = kod; }
    public Smeta getSmeta() { return smeta; }
    public void setSmeta(Smeta smeta) { this.smeta = smeta; }
    public SmetaAltBasliq getAltBasliq() { return altBasliq; }
    public void setAltBasliq(SmetaAltBasliq altBasliq) { this.altBasliq = altBasliq; }
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