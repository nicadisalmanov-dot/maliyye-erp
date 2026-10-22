package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MehsulRepository extends JpaRepository<Mehsul, Long> {

    // Müəyyən bir qrupda olan məhsulların sayını qaytarır (silmə qoruması üçün)
    long countByQrup_Id(Long qrupId);

    // Ada görə mal tapmaq üçün (eyni adlı mal varsa, onu istifadə etmək üçün)
    Mehsul findByAd(String ad);
}