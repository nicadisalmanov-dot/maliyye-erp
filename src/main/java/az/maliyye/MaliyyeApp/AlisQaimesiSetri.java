package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "alis_qaimesi_setirleri")
public class AlisQaimesiSetri {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "alis_qaimesi_id")
    private AlisQaimesi alisQaimesi;

    private String nov;

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

    // YENİ: Maya dəyəri və Mənfəət sahələri
    @Column(precision = 15, scale = 2)
    private BigDecimal mayaDeyeri = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal menfeet = BigDecimal.ZERO;

    // Xidmət adı (əgər xidmət entity yoxdursa)
    private String xidmetAdi;

    // Hesablar planı
    private String hesablarPlani;
    private String hesablarPlani2;
    private String elaveQeyd;

    public AlisQaimesiSetri() {}

    // Getterlər və Setterlər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AlisQaimesi getAlisQaimesi() { return alisQaimesi; }
    public void setAlisQaimesi(AlisQaimesi alisQaimesi) { this.alisQaimesi = alisQaimesi; }

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

    // YENİ: Maya dəyəri Getter/Setter
    public BigDecimal getMayaDeyeri() { return mayaDeyeri; }
    public void setMayaDeyeri(BigDecimal mayaDeyeri) { this.mayaDeyeri = mayaDeyeri; }

    // YENİ: Mənfəət Getter/Setter
    public BigDecimal getMenfeet() { return menfeet; }
    public void setMenfeet(BigDecimal menfeet) { this.menfeet = menfeet; }

    public String getXidmetAdi() { return xidmetAdi; }
    public void setXidmetAdi(String xidmetAdi) { this.xidmetAdi = xidmetAdi; }

    public String getHesablarPlani() { return hesablarPlani; }
    public void setHesablarPlani(String hesablarPlani) { this.hesablarPlani = hesablarPlani; }

    public String getHesablarPlani2() { return hesablarPlani2; }
    public void setHesablarPlani2(String hesablarPlani2) { this.hesablarPlani2 = hesablarPlani2; }

    public String getElaveQeyd() { return elaveQeyd; }
    public void setElaveQeyd(String elaveQeyd) { this.elaveQeyd = elaveQeyd; }
}