package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;

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
    private String rol; // "ADMIN" və ya "USER"

    // --- İCAZƏLƏR (Permissions) ---
    private boolean icazeKassa = false;
    private boolean icazeBank = false;
    private boolean icazeKontragent = false;
    private boolean icazeAnbar = false;
    private boolean icazeSatis = false;
    private boolean icazeSatinalma = false;
    private boolean icazeHesabat = false;

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
}