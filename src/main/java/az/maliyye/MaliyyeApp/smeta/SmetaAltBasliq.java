package az.maliyye.MaliyyeApp.smeta;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "smeta_alt_basliqlar")
public class SmetaAltBasliq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kod; // YENİ: Məs: "A.1", "D.1"

    @Column(nullable = false)
    private String ad;

    @ManyToOne
    @JoinColumn(name = "basliq_id", nullable = false)
    private SmetaBasliq basliq;

    @OneToMany(mappedBy = "altBasliq", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SmetaBendi> bəndlər;

    public double getYekunMebleg() {
        if (bəndlər == null || bəndlər.isEmpty()) return 0.0;
        return bəndlər.stream().mapToDouble(b -> b.getCemiMebleg() != null ? b.getCemiMebleg() : 0.0).sum();
    }

    // Getter və Setter-lər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKod() { return kod; }
    public void setKod(String kod) { this.kod = kod; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public SmetaBasliq getBasliq() { return basliq; }
    public void setBasliq(SmetaBasliq basliq) { this.basliq = basliq; }

    public List<SmetaBendi> getBəndlər() { return bəndlər; }
    public void setBəndlər(List<SmetaBendi> bəndlər) { this.bəndlər = bəndlər; }
}