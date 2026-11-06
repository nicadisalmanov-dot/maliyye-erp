package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "davamiyyet")
public class Davamiyyet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "isci_id", nullable = false)
    private Isci isci;

    @Column(nullable = false)
    private LocalDate tarix;

    private LocalTime girisVaxti;
    private LocalTime cixisVaxti;

    @Column(length = 30)
    private String status = "IS"; // IS, MEZUNIYYET, XESTELIK, QAYIB, BAYRAM, HEFTESONU

    @Column(length = 300)
    private String qeyd;

    public Davamiyyet() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Isci getIsci() { return isci; }
    public void setIsci(Isci isci) { this.isci = isci; }
    public LocalDate getTarix() { return tarix; }
    public void setTarix(LocalDate tarix) { this.tarix = tarix; }
    public LocalTime getGirisVaxti() { return girisVaxti; }
    public void setGirisVaxti(LocalTime girisVaxti) { this.girisVaxti = girisVaxti; }
    public LocalTime getCixisVaxti() { return cixisVaxti; }
    public void setCixisVaxti(LocalTime cixisVaxti) { this.cixisVaxti = cixisVaxti; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getQeyd() { return qeyd; }
    public void setQeyd(String qeyd) { this.qeyd = qeyd; }
}