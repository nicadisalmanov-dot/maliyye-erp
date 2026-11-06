package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;

@Entity
@Table(name = "olcu_vahidleri")
public class OlcuVahidi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String ad;

    @Column(length = 20)
    private String qisaAd;

    public OlcuVahidi() {}

    public OlcuVahidi(String ad, String qisaAd) {
        this.ad = ad;
        this.qisaAd = qisaAd;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public String getQisaAd() { return qisaAd; }
    public void setQisaAd(String qisaAd) { this.qisaAd = qisaAd; }
}