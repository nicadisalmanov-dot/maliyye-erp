package az.maliyye.MaliyyeApp.smeta;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "budceler")
public class Budce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "smeta_id", nullable = false)
    private Smeta smeta;

    private Double smetaMeblegi = 0.0;

    // Gözlənilməz xərclər faizi (məs: 10%)
    private Double gozlenilmezFaiz = 0.0;

    // Dolayı xərclər faizi / Əlavələr (məs: 5%)
    private Double dolayiXerclerFaizi = 0.0;

    // Yekun Büdcə Limiti = Smeta Məbləği + Gözlənilməz + Dolayı
    private Double yekunBudceMeblegi = 0.0;

    private String tesdiqleyenIstifadeci;
    private LocalDateTime tesdiqTarixi;

    public void hesablaVeYenile() {
        if (smeta != null) {
            this.smetaMeblegi = smeta.getUmumiMebleg();
            double gozlenilmez = (this.smetaMeblegi * (gozlenilmezFaiz != null ? gozlenilmezFaiz : 0.0)) / 100.0;
            double dolayi = (this.smetaMeblegi * (dolayiXerclerFaizi != null ? dolayiXerclerFaizi : 0.0)) / 100.0;
            this.yekunBudceMeblegi = this.smetaMeblegi + gozlenilmez + dolayi;
        }
    }

    // Getter və Setter-lər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Smeta getSmeta() { return smeta; }
    public void setSmeta(Smeta smeta) { this.smeta = smeta; }

    public Double getSmetaMeblegi() { return smetaMeblegi; }
    public void setSmetaMeblegi(Double smetaMeblegi) { this.smetaMeblegi = smetaMeblegi; }

    public Double getGozlenilmezFaiz() { return gozlenilmezFaiz; }
    public void setGozlenilmezFaiz(Double gozlenilmezFaiz) { this.gozlenilmezFaiz = gozlenilmezFaiz; }

    public Double getDolayiXerclerFaizi() { return dolayiXerclerFaizi; }
    public void setDolayiXerclerFaizi(Double dolayiXerclerFaizi) { this.dolayiXerclerFaizi = dolayiXerclerFaizi; }

    public Double getYekunBudceMeblegi() { return yekunBudceMeblegi; }
    public void setYekunBudceMeblegi(Double yekunBudceMeblegi) { this.yekunBudceMeblegi = yekunBudceMeblegi; }

    public String getTesdiqleyenIstifadeci() { return tesdiqleyenIstifadeci; }
    public void setTesdiqleyenIstifadeci(String tesdiqleyenIstifadeci) { this.tesdiqleyenIstifadeci = tesdiqleyenIstifadeci; }

    public LocalDateTime getTesdiqTarixi() { return tesdiqTarixi; }
    public void setTesdiqTarixi(LocalDateTime tesdiqTarixi) { this.tesdiqTarixi = tesdiqTarixi; }
}