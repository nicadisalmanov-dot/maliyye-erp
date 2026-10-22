package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;

@Entity
@Table(name = "anbarlar")
public class Anbar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ad; // Məsələn: Əsas Anbar, Mərkəzi Anbar

    @ManyToOne
    @JoinColumn(name = "layihe_id", nullable = false)
    private Layihe layihe; // Hansı layihəyə aiddir

    public Anbar() {}
    public Anbar(String ad, Layihe layihe) {
        this.ad = ad;
        this.layihe = layihe;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public Layihe getLayihe() { return layihe; }
    public void setLayihe(Layihe layihe) { this.layihe = layihe; }
}