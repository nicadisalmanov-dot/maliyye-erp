package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "istifadeciler")
public class Istifadeci {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String ad;

    @Column(nullable = false)
    private String rol;

    // --- Modul İcazələri ---
    private boolean icazeKassa = false;
    private boolean icazeBank = false;
    private boolean icazeKontragent = false;
    private boolean icazeAnbar = false;
    private boolean icazeSatis = false;
    private boolean icazeSatinalma = false;
    private boolean icazeHesabat = false;

    // --- Layihə və Anbar İcazələri ---
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_projects",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Layihe> allowedProjects;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_warehouses",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "warehouse_id")
    )
    private List<Anbar> allowedWarehouses;

    // --- Kassa və Bank Hesab İcazələri ---
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_kassa_hesablari",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "kassa_hesabi_id")
    )
    private List<KassaHesabi> allowedKassaHesablari;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_bank_hesablari",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "bank_hesabi_id")
    )
    private List<BankHesabi> allowedBankHesablari;

    public Istifadeci() {}

    // Getterlər və Setterlər
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isIcazeKassa() { return icazeKassa; }
    public void setIcazeKassa(boolean icazeKassa) { this.icazeKassa = icazeKassa; }

    public boolean isIcazeBank() { return icazeBank; }
    public void setIcazeBank(boolean icazeBank) { this.icazeBank = icazeBank; }

    public boolean isIcazeKontragent() { return icazeKontragent; }
    public void setIcazeKontragent(boolean icazeKontragent) { this.icazeKontragent = icazeKontragent; }

    public boolean isIcazeAnbar() { return icazeAnbar; }
    public void setIcazeAnbar(boolean icazeAnbar) { this.icazeAnbar = icazeAnbar; }

    public boolean isIcazeSatis() { return icazeSatis; }
    public void setIcazeSatis(boolean icazeSatis) { this.icazeSatis = icazeSatis; }

    public boolean isIcazeSatinalma() { return icazeSatinalma; }
    public void setIcazeSatinalma(boolean icazeSatinalma) { this.icazeSatinalma = icazeSatinalma; }

    public boolean isIcazeHesabat() { return icazeHesabat; }
    public void setIcazeHesabat(boolean icazeHesabat) { this.icazeHesabat = icazeHesabat; }

    public List<Layihe> getAllowedProjects() { return allowedProjects; }
    public void setAllowedProjects(List<Layihe> allowedProjects) { this.allowedProjects = allowedProjects; }

    public List<Anbar> getAllowedWarehouses() { return allowedWarehouses; }
    public void setAllowedWarehouses(List<Anbar> allowedWarehouses) { this.allowedWarehouses = allowedWarehouses; }

    public List<KassaHesabi> getAllowedKassaHesablari() { return allowedKassaHesablari; }
    public void setAllowedKassaHesablari(List<KassaHesabi> allowedKassaHesablari) { this.allowedKassaHesablari = allowedKassaHesablari; }

    public List<BankHesabi> getAllowedBankHesablari() { return allowedBankHesablari; }
    public void setAllowedBankHesablari(List<BankHesabi> allowedBankHesablari) { this.allowedBankHesablari = allowedBankHesablari; }
}