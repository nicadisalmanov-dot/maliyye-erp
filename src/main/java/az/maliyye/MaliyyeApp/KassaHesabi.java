package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;

@Entity
@Table(name = "kassa_hesablari")
public class KassaHesabi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ad;

    public KassaHesabi() {}
    public KassaHesabi(String ad) { this.ad = ad; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
}