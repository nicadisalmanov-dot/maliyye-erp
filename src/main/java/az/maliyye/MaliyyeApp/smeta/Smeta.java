package az.maliyye.MaliyyeApp.smeta;

import az.maliyye.MaliyyeApp.layiheler.Layihe;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "smetalar")
public class Smeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ad; // Məsələn: "Binanın Tikinti Smetası v1"

    private String versiya = "v1.0";

    private String valyuta = "AZN";

    // QARALAMA, TESDIQLENMIS, LEGV_EDILMIS
    private String status = "QARALAMA";

    // Layihə ilə əlaqə (Layihə ID)
    @ManyToOne
    @JoinColumn(name = "layihe_id", nullable = false)
    private Layihe layihe;

    @OneToMany(mappedBy = "smeta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SmetaBendi> bəndlər;

    @OneToOne(mappedBy = "smeta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Budce budce;

    private String yaradanIstifadeci;
    private LocalDateTime yaradilmaTarixi;

    public Smeta() {
        this.yaradilmaTarixi = LocalDateTime.now();
    }

    // Ümumi smeta məbləğini avtomatik hesablayan köməkçi metod
    public double getUmumiMebleg() {
        if (bəndlər == null || bəndlər.isEmpty()) {
            return 0.0;
        }
        return bəndlər.stream().mapToDouble(b -> b.getCemiMebleg() != null ? b.getCemiMebleg() : 0.0).sum();
    }

    // Getter və Setter-lər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public String getVersiya() { return versiya; }
    public void setVersiya(String versiya) { this.versiya = versiya; }

    public String getValyuta() { return valyuta; }
    public void setValyuta(String valyuta) { this.valyuta = valyuta; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Layihe getLayihe() { return layihe; }
    public void setLayihe(Layihe layihe) { this.layihe = layihe; }

    public List<SmetaBendi> getBəndlər() { return bəndlər; }
    public void setBəndlər(List<SmetaBendi> bəndlər) { this.bəndlər = bəndlər; }

    public Budce getBudce() { return budce; }
    public void setBudce(Budce budce) { this.budce = budce; }

    public String getYaradanIstifadeci() { return yaradanIstifadeci; }
    public void setYaradanIstifadeci(String yaradanIstifadeci) { this.yaradanIstifadeci = yaradanIstifadeci; }

    public LocalDateTime getYaradilmaTarixi() { return yaradilmaTarixi; }
    public void setYaradilmaTarixi(LocalDateTime yaradilmaTarixi) { this.yaradilmaTarixi = yaradilmaTarixi; }
}