package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;

@Entity
@Table(name = "mal_tipleri")
public class MalTipi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String ad;

    public MalTipi() {}

    public MalTipi(String ad) {
        this.ad = ad;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
}