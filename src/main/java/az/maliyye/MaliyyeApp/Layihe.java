package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "layiheler")
public class Layihe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ad; // Məsələn: L-01 Port Baku

    // Layihə silinəndə ona bağlı anbarlar da silinsin (Cascade)
    @OneToMany(mappedBy = "layihe", cascade = CascadeType.ALL)
    private List<Anbar> anbarlar;

    public Layihe() {}
    public Layihe(String ad) { this.ad = ad; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public List<Anbar> getAnbarlar() { return anbarlar; }
    public void setAnbarlar(List<Anbar> anbarlar) { this.anbarlar = anbarlar; }
}