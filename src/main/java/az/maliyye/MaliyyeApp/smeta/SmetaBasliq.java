package az.maliyye.MaliyyeApp.smeta;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "smeta_basliqlari")
public class SmetaBasliq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kod; // YENİ: Məs: "A", "B", "D+E"

    @Column(nullable = false)
    private String ad; // Məs: "DOLAYI XƏRCLƏR"

    @ManyToOne
    @JoinColumn(name = "smeta_id", nullable = false)
    private Smeta smeta;

    @OneToMany(mappedBy = "basliq", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SmetaAltBasliq> altBasliqlar;

    // Böyük bölmənin yekun cəmi (alt başlıqların cəmi)
    public double getYekunMebleg() {
        if (altBasliqlar == null || altBasliqlar.isEmpty()) return 0.0;
        return altBasliqlar.stream().mapToDouble(SmetaAltBasliq::getYekunMebleg).sum();
    }

    // Getter və Setter-lər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKod() { return kod; }
    public void setKod(String kod) { this.kod = kod; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public Smeta getSmeta() { return smeta; }
    public void setSmeta(Smeta smeta) { this.smeta = smeta; }

    public List<SmetaAltBasliq> getAltBasliqlar() { return altBasliqlar; }
    public void setAltBasliqlar(List<SmetaAltBasliq> altBasliqlar) { this.altBasliqlar = altBasliqlar; }
}