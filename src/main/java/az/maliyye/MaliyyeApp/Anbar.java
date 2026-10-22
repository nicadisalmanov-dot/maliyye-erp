package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "anbarlar")
public class Anbar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ad;

    // Bir anbar bir neçə layihəyə xidmət edə bilər
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "anbar_layiheler",
            joinColumns = @JoinColumn(name = "anbar_id"),
            inverseJoinColumns = @JoinColumn(name = "layihe_id")
    )
    private List<Layihe> layiheler;

    public Anbar() {}

    public Anbar(String ad) {
        this.ad = ad;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public List<Layihe> getLayiheler() { return layiheler; }
    public void setLayiheler(List<Layihe> layiheler) { this.layiheler = layiheler; }
}