package az.maliyye.MaliyyeApp.smeta;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "faktiki_xercler")
public class FaktikiXerc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "smeta_bendi_id", nullable = false)
    private SmetaBendi smetaBendi;

    private Double mebleg = 0.0;

    // Mənbə: "KASSA", "BANK", "ANBAR"
    private String menbe;

    private Long menbeId; // KassaEmeliyyati və ya AnbarEmeliyyati ID-si

    private String tesvir;

    private LocalDateTime tarix;

    public FaktikiXerc() {
        this.tarix = LocalDateTime.now();
    }

    // Getter və Setter-lər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SmetaBendi getSmetaBendi() { return smetaBendi; }
    public void setSmetaBendi(SmetaBendi smetaBendi) { this.smetaBendi = smetaBendi; }

    public Double getMebleg() { return mebleg; }
    public void setMebleg(Double mebleg) { this.mebleg = mebleg; }

    public String getMenbe() { return menbe; }
    public void setMenbe(String menbe) { this.menbe = menbe; }

    public Long getMenbeId() { return menbeId; }
    public void setMenbeId(Long menbeId) { this.menbeId = menbeId; }

    public String getTesvir() { return tesvir; }
    public void setTesvir(String tesvir) { this.tesvir = tesvir; }

    public LocalDateTime getTarix() { return tarix; }
    public void setTarix(LocalDateTime tarix) { this.tarix = tarix; }
}