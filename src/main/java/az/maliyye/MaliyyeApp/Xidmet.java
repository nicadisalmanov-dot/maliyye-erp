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

    @Column(length = 50)
    private String nov;

    @Column(length = 20)
    private String vahid;

    @Column(precision = 15, scale = 2)
    private BigDecimal qiymet;

    // YENİ: Yaradılma və Düzəliş tarixi, Yaradan istifadəçi
    @Column(name = "yaradilma_tarixi")
    private LocalDateTime yaradilmaTarixi;

    @Column(name = "duzelis_tarixi")
    private LocalDateTime duzelisTarixi;

    @ManyToOne
    @JoinColumn(name = "yaradan_istifadeci_id")
    private Istifadeci yaradanIstifadeci;

    public Xidmet() {
        this.yaradilmaTarixi = LocalDateTime.now();
    }

    public Xidmet(String ad, XidmetQrupu qrup, String nov, String vahid, BigDecimal qiymet) {
        this.ad = ad;
        this.qrup = qrup;
        this.nov = nov;
        this.vahid = vahid;
        this.qiymet = qiymet;
        this.yaradilmaTarixi = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public XidmetQrupu getQrup() { return qrup; }
    public void setQrup(XidmetQrupu qrup) { this.qrup = qrup; }
    public String getNov() { return nov; }
    public void setNov(String nov) { this.nov = nov; }
    public String getVahid() { return vahid; }
    public void setVahid(String vahid) { this.vahid = vahid; }
    public BigDecimal getQiymet() { return qiymet; }
    public void setQiymet(BigDecimal qiymet) { this.qiymet = qiymet; }
    public LocalDateTime getYaradilmaTarixi() { return yaradilmaTarixi; }
    public void setYaradilmaTarixi(LocalDateTime yaradilmaTarixi) { this.yaradilmaTarixi = yaradilmaTarixi; }
    public LocalDateTime getDuzelisTarixi() { return duzelisTarixi; }
    public void setDuzelisTarixi(LocalDateTime duzelisTarixi) { this.duzelisTarixi = duzelisTarixi; }
    public Istifadeci getYaradanIstifadeci() { return yaradanIstifadeci; }
    public void setYaradanIstifadeci(Istifadeci yaradanIstifadeci) { this.yaradanIstifadeci = yaradanIstifadeci; }
}