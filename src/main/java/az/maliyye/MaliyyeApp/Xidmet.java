package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "xidmetler")
public class Xidmet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String ad;

    @ManyToOne
    @JoinColumn(name = "qrup_id", nullable = false)
    private XidmetQrupu qrup;

    @ManyToOne
    @JoinColumn(name = "tip_id")
    private XidmetTipi tip;

    @Column(length = 20)
    private String vahid = "ədəd";

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal qiymet;

    @Column(name = "yaradilma_tarixi")
    private LocalDateTime yaradilmaTarixi;

    @ManyToOne
    @JoinColumn(name = "yaradan_istifadeci_id")
    private Istifadeci yaradanIstifadeci;

    public Xidmet() {
        this.yaradilmaTarixi = LocalDateTime.now();
    }

    public Xidmet(String ad, XidmetQrupu qrup, XidmetTipi tip, String vahid, BigDecimal qiymet) {
        this.ad = ad;
        this.qrup = qrup;
        this.tip = tip;
        this.vahid = vahid;
        this.qiymet = qiymet;
        this.yaradilmaTarixi = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public XidmetQrupu getQrup() { return qrup; }
    public void setQrup(XidmetQrupu qrup) { this.qrup = qrup; }
    public XidmetTipi getTip() { return tip; }
    public void setTip(XidmetTipi tip) { this.tip = tip; }
    public String getVahid() { return vahid; }
    public void setVahid(String vahid) { this.vahid = vahid; }
    public BigDecimal getQiymet() { return qiymet; }
    public void setQiymet(BigDecimal qiymet) { this.qiymet = qiymet; }
    public LocalDateTime getYaradilmaTarixi() { return yaradilmaTarixi; }
    public void setYaradilmaTarixi(LocalDateTime yaradilmaTarixi) { this.yaradilmaTarixi = yaradilmaTarixi; }
    public Istifadeci getYaradanIstifadeci() { return yaradanIstifadeci; }
    public void setYaradanIstifadeci(Istifadeci yaradanIstifadeci) { this.yaradanIstifadeci = yaradanIstifadeci; }
}