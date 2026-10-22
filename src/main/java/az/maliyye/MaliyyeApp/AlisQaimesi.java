package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "alis_qaimeleri")
public class AlisQaimesi {

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

    // YENİ: Qaiməni yaradan istifadəçi
    @ManyToOne
    @JoinColumn(name = "yaradan_istifadeci_id")
    private Istifadeci yaradanIstifadeci;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal umumiMebleg = BigDecimal.ZERO;

    @Column(name = "yaradilma_tarixi")
    private LocalDateTime yaradilmaTarixi;

    @Column(name = "duzelis_tarixi")
    private LocalDateTime duzelisTarixi;

    @Column(name = "silinib")
    private Boolean silinib = false;

    @Column(name = "silinme_tarixi")
    private LocalDateTime silinmeTarixi;

    @OneToMany(mappedBy = "alisQaimesi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlisQaimesiSetri> setirler;

    public AlisQaimesi() {
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

    // YENİ: Getter və Setter
    public Istifadeci getYaradanIstifadeci() { return yaradanIstifadeci; }
    public void setYaradanIstifadeci(Istifadeci yaradanIstifadeci) { this.yaradanIstifadeci = yaradanIstifadeci; }

    public BigDecimal getUmumiMebleg() { return umumiMebleg; }
    public void setUmumiMebleg(BigDecimal umumiMebleg) { this.umumiMebleg = umumiMebleg; }

    public List<AlisQaimesiSetri> getSetirler() { return setirler; }
    public void setSetirler(List<AlisQaimesiSetri> setirler) { this.setirler = setirler; }

    public LocalDateTime getYaradilmaTarixi() { return yaradilmaTarixi; }
    public void setYaradilmaTarixi(LocalDateTime yaradilmaTarixi) { this.yaradilmaTarixi = yaradilmaTarixi; }

    public LocalDateTime getDuzelisTarixi() { return duzelisTarixi; }
    public void setDuzelisTarixi(LocalDateTime duzelisTarixi) { this.duzelisTarixi = duzelisTarixi; }

    public Boolean getSilinib() { return silinib; }
    public void setSilinib(Boolean silinib) { this.silinib = silinib; }

    public LocalDateTime getSilinmeTarixi() { return silinmeTarixi; }
    public void setSilinmeTarixi(LocalDateTime silinmeTarixi) { this.silinmeTarixi = silinmeTarixi; }
}