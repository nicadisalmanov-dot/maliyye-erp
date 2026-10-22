package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "alis_qaimeleri_setirleri")
public class AlisQaimesiSetri {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "alis_qaimesi_id", nullable = false)
    private AlisQaimesi alisQaimesi;

    // YENİ: Hər sətir üçün Layihə
    @ManyToOne
    @JoinColumn(name = "layihe_id")
    private Layihe layihe;

    @Column(nullable = false, length = 20)
    private String nov;

    @ManyToOne
    @JoinColumn(name = "mehsul_id")
    private Mehsul mehsul;

    @Column(length = 200)
    private String xidmetAdi;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal miqdar;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal vahidQiymeti;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal mebleg;

    @Column(length = 50)
    private String hesablarPlani;

    @Column(length = 50)
    private String hesablarPlani2;

    @Column(length = 200)
    private String elaveQeyd;

    public AlisQaimesiSetri() {}

    // Getterlər və Setterlər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AlisQaimesi getAlisQaimesi() { return alisQaimesi; }
    public void setAlisQaimesi(AlisQaimesi alisQaimesi) { this.alisQaimesi = alisQaimesi; }
    public Layihe getLayihe() { return layihe; }
    public void setLayihe(Layihe layihe) { this.layihe = layihe; }
    public String getNov() { return nov; }
    public void setNov(String nov) { this.nov = nov; }
    public Mehsul getMehsul() { return mehsul; }
    public void setMehsul(Mehsul mehsul) { this.mehsul = mehsul; }
    public String getXidmetAdi() { return xidmetAdi; }
    public void setXidmetAdi(String xidmetAdi) { this.xidmetAdi = xidmetAdi; }
    public BigDecimal getMiqdar() { return miqdar; }
    public void setMiqdar(BigDecimal miqdar) { this.miqdar = miqdar; }
    public BigDecimal getVahidQiymeti() { return vahidQiymeti; }
    public void setVahidQiymeti(BigDecimal vahidQiymeti) { this.vahidQiymeti = vahidQiymeti; }
    public BigDecimal getMebleg() { return mebleg; }
    public void setMebleg(BigDecimal mebleg) { this.mebleg = mebleg; }
    public String getHesablarPlani() { return hesablarPlani; }
    public void setHesablarPlani(String hesablarPlani) { this.hesablarPlani = hesablarPlani; }
    public String getHesablarPlani2() { return hesablarPlani2; }
    public void setHesablarPlani2(String hesablarPlani2) { this.hesablarPlani2 = hesablarPlani2; }
    public String getElaveQeyd() { return elaveQeyd; }
    public void setElaveQeyd(String elaveQeyd) { this.elaveQeyd = elaveQeyd; }
}