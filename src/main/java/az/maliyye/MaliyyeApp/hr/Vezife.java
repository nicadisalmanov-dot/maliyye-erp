package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "vezifeler")
public class Vezife {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String ad;

    @Column(length = 500)
    private String tesvir;

    @Column(precision = 15, scale = 2)
    private BigDecimal esasEmekHaqqi = BigDecimal.ZERO;

    public Vezife() {}

    public Vezife(String ad) { this.ad = ad; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public String getTesvir() { return tesvir; }
    public void setTesvir(String tesvir) { this.tesvir = tesvir; }
    public BigDecimal getEsasEmekHaqqi() { return esasEmekHaqqi; }
    public void setEsasEmekHaqqi(BigDecimal esasEmekHaqqi) { this.esasEmekHaqqi = esasEmekHaqqi; }
}