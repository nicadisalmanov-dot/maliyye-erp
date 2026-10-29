package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "mezuniyyetler")
public class Mezuniyyet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "isci_id", nullable = false)
    private Isci isci;

    @Column(nullable = false, length = 30)
    private String nov; // ILLIK, XESTELIK, EZAMIYYET, ANALIQ, ODOU

    @Column(nullable = false)
    private LocalDate baslamaTarixi;

    @Column(nullable = false)
    private LocalDate bitmeTarixi;

    private Integer gunSayi;

    @Column(length = 500)
    private String sebeb;

    @Column(length = 30)
    private String status = "GOZLEYIR"; // GOZLEYIR, TESDIQLENDI, REDD_EDILDI

    public Mezuniyyet() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Isci getIsci() { return isci; }
    public void setIsci(Isci isci) { this.isci = isci; }
    public String getNov() { return nov; }
    public void setNov(String nov) { this.nov = nov; }
    public LocalDate getBaslamaTarixi() { return baslamaTarixi; }
    public void setBaslamaTarixi(LocalDate baslamaTarixi) { this.baslamaTarixi = baslamaTarixi; }
    public LocalDate getBitmeTarixi() { return bitmeTarixi; }
    public void setBitmeTarixi(LocalDate bitmeTarixi) { this.bitmeTarixi = bitmeTarixi; }
    public Integer getGunSayi() { return gunSayi; }
    public void setGunSayi(Integer gunSayi) { this.gunSayi = gunSayi; }
    public String getSebeb() { return sebeb; }
    public void setSebeb(String sebeb) { this.sebeb = sebeb; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}