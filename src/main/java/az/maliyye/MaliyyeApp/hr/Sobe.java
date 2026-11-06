package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "sobeler")
public class Sobe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String ad;

    @Column(length = 500)
    private String tesvir;

    @ManyToOne
    @JoinColumn(name = "rehber_id")
    private Isci rehber; // Şöbə rəhbəri

    @OneToMany(mappedBy = "sobe")
    private List<Isci> isciler;

    public Sobe() {}

    public Sobe(String ad) { this.ad = ad; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public String getTesvir() { return tesvir; }
    public void setTesvir(String tesvir) { this.tesvir = tesvir; }
    public Isci getRehber() { return rehber; }
    public void setRehber(Isci rehber) { this.rehber = rehber; }
    public List<Isci> getIsciler() { return isciler; }
    public void setIsciler(List<Isci> isciler) { this.isciler = isciler; }
}