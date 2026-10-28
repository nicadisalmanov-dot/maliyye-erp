package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;

@Entity
@Table(name = "xidmet_tipleri")
public class XidmetTipi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String ad;

    public XidmetTipi() {}

    public XidmetTipi(String ad) {
        this.ad = ad;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
}