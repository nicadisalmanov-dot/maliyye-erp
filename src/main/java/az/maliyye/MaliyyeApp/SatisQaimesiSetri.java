package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "satis_qaimeleri_setirleri")
public class SatisQaimesiSetri {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "satis_qaimesi_id", nullable = false)
    private SatisQaimesi satisQaimesi;

    @ManyToOne
    @JoinColumn(name = "layihe_id")
    private Layihe layihe;

    @Column(nullable = false, length = 50)
    private String nov;

    @ManyToOne
    @JoinColumn(name = "mehsul_id")
    private Mehsul mehsul;

    @ManyToOne
    @JoinColumn(name = "xidmet_id")
    private Xidmet xidmet;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal miqdar;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal vahidQiymeti; // Satış qiyməti

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal mebleg; // Ümumi satış məbləği

    // YENİ: Maya dəyəri və Mənfəət
    @Column(precision = 15, scale = 2)
    private BigDecimal mayaDeyeri; // Orta dəyər * miqdar

    @Column(precision = 15, scale = 2)
    private BigDecimal menfeet; // Mebleg - MayaDeyeri

    @Column(length = 50)
    private String hesablarPlani;

    @Column(length = 200)
    private String elaveQeyd;

    public SatisQaimesiSetri() {}

    // Getterlər və Setterlər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SatisQaimesi getSatisQaimesi() { return satisQaimesi; }
    public void setSatisQaimesi(SatisQaimesi satisQaimesi) { this.satisQaimesi = satisQaimesi; }
    public Layihe getLayihe() { return layihe; }
    public void setLayihe(Layihe layihe) { this.layihe = layihe; }
    public String getNov() { return nov; }
    public void setNov(String nov) { this.nov = nov; }
    public Mehsul getMehsul() { return mehsul; }
    public void setMehsul(Mehsul mehsul) { this.mehsul = mehsul; }
    public Xidmet getXidmet() { return xidmet; }
    public void setXidmet(Xidmet xidmet) { this.xidmet = xidmet; }
    public BigDecimal getMiqdar() { return miqdar; }
    public void setMiqdar(BigDecimal miqdar) { this.miqdar = miqdar; }
    public BigDecimal getVahidQiymeti() { return vahidQiymeti; }
    public void setVahidQiymeti(BigDecimal vahidQiymeti) { this.vahidQiymeti = vahidQiymeti; }
    public BigDecimal getMebleg() { return mebleg; }
    public void setMebleg(BigDecimal mebleg) { this.mebleg = mebleg; }
    public BigDecimal getMayaDeyeri() { return mayaDeyeri; }
    public void setMayaDeyeri(BigDecimal mayaDeyeri) { this.mayaDeyeri = mayaDeyeri; }
    public BigDecimal getMenfeet() { return menfeet; }
    public void setMenfeet(BigDecimal menfeet) { this.menfeet = menfeet; }
    public String getHesablarPlani() { return hesablarPlani; }
    public void setHesablarPlani(String hesablarPlani) { this.hesablarPlani = hesablarPlani; }
    public String getElaveQeyd() { return elaveQeyd; }
    public void setElaveQeyd(String elaveQeyd) { this.elaveQeyd = elaveQeyd; }
}