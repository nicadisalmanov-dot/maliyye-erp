package az.maliyye.MaliyyeApp;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "layiheler")
public class Layihe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ad;

    // Anbarlarla əks əlaqə (Many-to-Many)
    // Bu, Spring-ə deyir ki, "əgər Layihə silinirsə, onun Anbarlarla olan əlaqəsini də bil"
    @ManyToMany(mappedBy = "layiheler")
    private List<Anbar> anbarlar;

    public Layihe() {}

    public Layihe(String ad) {
        this.ad = ad;
    }

    // Getterlər və Setterlər
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public List<Anbar> getAnbarlar() {
        return anbarlar;
    }

    public void setAnbarlar(List<Anbar> anbarlar) {
        this.anbarlar = anbarlar;
    }
}