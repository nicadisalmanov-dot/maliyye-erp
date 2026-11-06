package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bayramlar")
public class Bayram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String ad;

    @Column(nullable = false)
    private LocalDate tarix;

    @Column(length = 30)
    private String nov = "RESMI"; // RESMI, DINI, BEYNELXALQ

    public Bayram() {}

    public Bayram(String ad, LocalDate tarix) {
        this.ad = ad;
        this.tarix = tarix;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public LocalDate getTarix() { return tarix; }
    public void setTarix(LocalDate tarix) { this.tarix = tarix; }
    public String getNov() { return nov; }
    public void setNov(String nov) { this.nov = nov; }
}