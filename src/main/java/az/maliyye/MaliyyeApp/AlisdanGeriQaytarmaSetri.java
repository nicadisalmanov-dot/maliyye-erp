package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "alisdan_geri_qaytarma_setirleri")
public class AlisdanGeriQaytarmaSetri {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "geri_qaytarma_id")
    private AlisdanGeriQaytarma alisdanGeriQaytarma;

    private String nov; // MAL və ya XIDMET

    @ManyToOne
    @JoinColumn(name = "mehsul_id")
    private Mehsul mehsul;

    @ManyToOne
    @JoinColumn(name = "xidmet_id")
    private Xidmet xidmet;

    @ManyToOne
    @JoinColumn(name = "layihe_id")
    private Layihe layihe;

    private BigDecimal miqdar;
    private BigDecimal vahidQiymeti;
    private BigDecimal mebleg;

    private String xidmetAdi;
    private String hesablarPlani;
    private String elaveQeyd;

    public AlisdanGeriQaytarmaSetri() {}

    // Getterlər və Setterlər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AlisdanGeriQaytarma getAlisdanGeriQaytarma() { return alisdanGeriQaytarma; }
    public void setAlisdanGeriQaytarma(AlisdanGeriQaytarma alisdanGeriQaytarma) { this.alisdanGeriQaytarma = alisdanGeriQaytarma; }
    public String getNov() { return nov; }
    public void setNov(String nov) { this.nov = nov; }
    public Mehsul getMehsul() { return mehsul; }
    public void setMehsul(Mehsul mehsul) { this.mehsul = mehsul; }
    public Xidmet getXidmet() { return xidmet; }
    public void setXidmet(Xidmet xidmet) { this.xidmet = xidmet; }
    public Layihe getLayihe() { return layihe; }
    public void setLayihe(Layihe layihe) { this.layihe = layihe; }
    public BigDecimal getMiqdar() { return miqdar; }
    public void setMiqdar(BigDecimal miqdar) { this.miqdar = miqdar; }
    public BigDecimal getVahidQiymeti() { return vahidQiymeti; }
    public void setVahidQiymeti(BigDecimal vahidQiymeti) { this.vahidQiymeti = vahidQiymeti; }
    public BigDecimal getMebleg() { return mebleg; }
    public void setMebleg(BigDecimal mebleg) { this.mebleg = mebleg; }
    public String getXidmetAdi() { return xidmetAdi; }
    public void setXidmetAdi(String xidmetAdi) { this.xidmetAdi = xidmetAdi; }
    public String getHesablarPlani() { return hesablarPlani; }
    public void setHesablarPlani(String hesablarPlani) { this.hesablarPlani = hesablarPlani; }
    public String getElaveQeyd() { return elaveQeyd; }
    public void setElaveQeyd(String elaveQeyd) { this.elaveQeyd = elaveQeyd; }
}