package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;

@Entity
@Table(name = "kontragentler")
public class Kontragent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String ad; // Şirkətin və ya şəxsin adı

    @Column(length = 50)
    private String telefon;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String unvan;

    @Column(nullable = false, length = 20)
    private String nov; // "MUSTERI", "TECHIZATCI" və ya "HER_IKISI"

    public Kontragent() {}

    public Kontragent(String ad, String telefon, String email, String unvan, String nov) {
        this.ad = ad;
        this.telefon = telefon;
        this.email = email;
        this.unvan = unvan;
        this.nov = nov;
    }

    // Getterlər
    public Long getId() { return id; }
    public String getAd() { return ad; }
    public String getTelefon() { return telefon; }
    public String getEmail() { return email; }
    public String getUnvan() { return unvan; }
    public String getNov() { return nov; }

    // Setterlər
    public void setId(Long id) { this.id = id; }
    public void setAd(String ad) { this.ad = ad; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public void setEmail(String email) { this.email = email; }
    public void setUnvan(String unvan) { this.unvan = unvan; }
    public void setNov(String nov) { this.nov = nov; }
}