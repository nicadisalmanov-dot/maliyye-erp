package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;

@Entity
@Table(name = "mehsul_qruplari")
public class MehsulQrupu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String ad;

    // Boş konstruktor
    public MehsulQrupu() {}

    public MehsulQrupu(String ad) {
        this.ad = ad;
    }

    public Long getId() { return id; }
    public String getAd() { return ad; }
    public void setId(Long id) { this.id = id; }
    public void setAd(String ad) { this.ad = ad; }
}