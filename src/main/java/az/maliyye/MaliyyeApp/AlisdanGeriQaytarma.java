package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "alisdan_geri_qaytarma")
public class AlisdanGeriQaytarma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String senedNomresi;

    @Column(nullable = false)
    private LocalDate tarix;

    @ManyToOne
    @JoinColumn(name = "anbar_id", nullable = false)
    private Anbar anbar;

    @ManyToOne
    @JoinColumn(name = "kontragent_id", nullable = false)
    private Kontragent kontragent;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal umumiMebleg = BigDecimal.ZERO;

    // --- Audit və Arxiv Sahələri ---
    @Column(name = "yaradilma_tarixi")
    private LocalDateTime yaradilmaTarixi;

    @Column(name = "silinib")
    private Boolean silinib = false;

    @Column(name = "silinme_tarixi")
    private LocalDateTime silinmeTarixi;

    @ManyToOne
    @JoinColumn(name = "yaradan_istifadeci_id")
    private Istifadeci yaradanIstifadeci;

    @ManyToOne
    @JoinColumn(name = "silen_istifadeci_id")
    private Istifadeci silenIstifadeci;

    @OneToMany(mappedBy = "alisdanGeriQaytarma", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlisdanGeriQaytarmaSetri> setirler;

    public AlisdanGeriQaytarma() {
        this.yaradilmaTarixi = LocalDateTime.now();
        this.silinib = false;
    }

    // Getterlər və Setterlər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSenedNomresi() { return senedNomresi; }
    public void setSenedNomresi(String senedNomresi) { this.senedNomresi = senedNomresi; }
    public LocalDate getTarix() { return tarix; }
    public void setTarix(LocalDate tarix) { this.tarix = tarix; }
    public Anbar getAnbar() { return anbar; }
    public void setAnbar(Anbar anbar) { this.anbar = anbar; }
    public Kontragent getKontragent() { return kontragent; }
    public void setKontragent(Kontragent kontragent) { this.kontragent = kontragent; }
    public BigDecimal getUmumiMebleg() { return umumiMebleg; }
    public void setUmumiMebleg(BigDecimal umumiMebleg) { this.umumiMebleg = umumiMebleg; }
    public LocalDateTime getYaradilmaTarixi() { return yaradilmaTarixi; }
    public void setYaradilmaTarixi(LocalDateTime yaradilmaTarixi) { this.yaradilmaTarixi = yaradilmaTarixi; }
    public Boolean getSilinib() { return silinib; }
    public void setSilinib(Boolean silinib) { this.silinib = silinib; }
    public LocalDateTime getSilinmeTarixi() { return silinmeTarixi; }
    public void setSilinmeTarixi(LocalDateTime silinmeTarixi) { this.silinmeTarixi = silinmeTarixi; }
    public Istifadeci getYaradanIstifadeci() { return yaradanIstifadeci; }
    public void setYaradanIstifadeci(Istifadeci yaradanIstifadeci) { this.yaradanIstifadeci = yaradanIstifadeci; }
    public Istifadeci getSilenIstifadeci() { return silenIstifadeci; }
    public void setSilenIstifadeci(Istifadeci silenIstifadeci) { this.silenIstifadeci = silenIstifadeci; }
    public List<AlisdanGeriQaytarmaSetri> getSetirler() { return setirler; }
    public void setSetirler(List<AlisdanGeriQaytarmaSetri> setirler) { this.setirler = setirler; }
}