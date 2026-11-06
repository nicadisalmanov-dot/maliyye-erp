package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "istehsal_teqvimi", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tarix"})
})
public class IstehsalTegvimi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate tarix;

    @Column(nullable = false, length = 30)
    private String gunNovu; // ISH_GUNU, HEFTE_SONU, BAYRAM, YERDEYISHME

    @Column(length = 150)
    private String qeyd; // Məsələn: "Novruz Bayramı", "Şənbə iş günü"

    public IstehsalTegvimi() {}

    public IstehsalTegvimi(LocalDate tarix, String gunNovu, String qeyd) {
        this.tarix = tarix;
        this.gunNovu = gunNovu;
        this.qeyd = qeyd;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getTarix() { return tarix; }
    public void setTarix(LocalDate tarix) { this.tarix = tarix; }
    public String getGunNovu() { return gunNovu; }
    public void setGunNovu(String gunNovu) { this.gunNovu = gunNovu; }
    public String getQeyd() { return qeyd; }
    public void setQeyd(String qeyd) { this.qeyd = qeyd; }
}