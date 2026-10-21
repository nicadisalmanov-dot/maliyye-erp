package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bank_emeliyyatlar")
public class BankEmeliyyati {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate tarix;

    @Column(nullable = false, length = 50)
    private String senedNomresi;

    @Column(nullable = false, length = 300)
    private String tesvir;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal mebleg;

    @Column(nullable = false, length = 20)
    private String nov; // "MEDAXIL" və ya "MEXARIC"

    @Column(nullable = false, length = 100)
    private String bankHesabi;

    // YENİ ƏLAVƏ: Kontragent
    @ManyToOne
    @JoinColumn(name = "kontragent_id")
    private Kontragent kontragent;

    public BankEmeliyyati() {}

    public BankEmeliyyati(LocalDate tarix, String senedNomresi, String tesvir, BigDecimal mebleg, String nov, String bankHesabi) {
        this.tarix = tarix;
        this.senedNomresi = senedNomresi;
        this.tesvir = tesvir;
        this.mebleg = mebleg;
        this.nov = nov;
        this.bankHesabi = bankHesabi;
    }

    public Long getId() { return id; }
    public LocalDate getTarix() { return tarix; }
    public String getSenedNomresi() { return senedNomresi; }
    public String getTesvir() { return tesvir; }
    public BigDecimal getMebleg() { return mebleg; }
    public String getNov() { return nov; }
    public String getBankHesabi() { return bankHesabi; }
    public Kontragent getKontragent() { return kontragent; }

    public void setId(Long id) { this.id = id; }
    public void setTarix(LocalDate tarix) { this.tarix = tarix; }
    public void setSenedNomresi(String senedNomresi) { this.senedNomresi = senedNomresi; }
    public void setTesvir(String tesvir) { this.tesvir = tesvir; }
    public void setMebleg(BigDecimal mebleg) { this.mebleg = mebleg; }
    public void setNov(String nov) { this.nov = nov; }
    public void setBankHesabi(String bankHesabi) { this.bankHesabi = bankHesabi; }
    public void setKontragent(Kontragent kontragent) { this.kontragent = kontragent; }

    public boolean isMedaxil() { return "MEDAXIL".equals(nov); }
    public boolean isMexaric() { return "MEXARIC".equals(nov); }
}