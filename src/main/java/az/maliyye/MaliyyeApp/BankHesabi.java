package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;

@Entity
@Table(name = "bank_hesablari")
public class BankHesabi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ad; // Məsələn: Kapital Bank - AZ12...

    public BankHesabi() {}
    public BankHesabi(String ad) { this.ad = ad; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
}