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
    private String rol;

    public Istifadeci() {}

    public Istifadeci(String username, String password, String ad, String rol) {
        this.username = username;
        this.password = password;
        this.ad = ad;
        this.rol = rol;
    }

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
}