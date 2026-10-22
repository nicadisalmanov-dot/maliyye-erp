package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MehsulRepository extends JpaRepository<Mehsul, Long> {

    // YENİ: Müəyyən bir qrupda olan məhsulların sayını qaytarır
    long countByQrup_Id(Long qrupId);
}