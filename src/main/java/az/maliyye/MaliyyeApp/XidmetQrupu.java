package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "xidmet_qruplari")
public class XidmetQrupu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String ad;

    @Column(name = "yaradilma_tarixi")
    private LocalDateTime yaradilmaTarixi;

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
}