package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "emeliyyatlar")
public class Emeliyyat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate tarix;

    @Column(nullable = false, length = 200)
    private String tesvir;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal mebleg;

    // Boş konstruktor (JPA tələb edir)
    public Emeliyyat() {
    }

    public Emeliyyat(LocalDate tarix, String tesvir, BigDecimal mebleg) {
        this.tarix = tarix;
        this.tesvir = tesvir;
        this.mebleg = mebleg;
    }

    // Getter-lər
    public Long getId() { return id; }
    public LocalDate getTarix() { return tarix; }
    public String getTesvir() { return tesvir; }
    public BigDecimal getMebleg() { return mebleg; }

    // Setter-lər
    public void setId(Long id) { this.id = id; }
    public void setTarix(LocalDate tarix) { this.tarix = tarix; }
    public void setTesvir(String tesvir) { this.tesvir = tesvir; }
    public void setMebleg(BigDecimal mebleg) { this.mebleg = mebleg; }
}