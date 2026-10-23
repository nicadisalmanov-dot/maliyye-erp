package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "xidmet_qruplari")
public class XidmetQrupu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String ad;

    // YENİ: Yaradılma tarixi və Yaradan istifadəçi
    @Column(name = "yaradilma_tarixi")
    private LocalDateTime yaradilmaTarixi;

    @ManyToOne
    @JoinColumn(name = "yaradan_istifadeci_id")
    private Istifadeci yaradanIstifadeci;

    public XidmetQrupu() {
        this.yaradilmaTarixi = LocalDateTime.now();
    }

    public XidmetQrupu(String ad) {
        this.ad = ad;
        this.yaradilmaTarixi = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public LocalDateTime getYaradilmaTarixi() { return yaradilmaTarixi; }
    public void setYaradilmaTarixi(LocalDateTime yaradilmaTarixi) { this.yaradilmaTarixi = yaradilmaTarixi; }
    public Istifadeci getYaradanIstifadeci() { return yaradanIstifadeci; }
    public void setYaradanIstifadeci(Istifadeci yaradanIstifadeci) { this.yaradanIstifadeci = yaradanIstifadeci; }
}