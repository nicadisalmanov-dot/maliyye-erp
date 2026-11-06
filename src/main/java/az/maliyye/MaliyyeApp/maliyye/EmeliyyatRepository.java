package az.maliyye.MaliyyeApp.maliyye;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmeliyyatRepository extends JpaRepository<Emeliyyat, Long> {

    // Təhtəlhesab şəxsə bağlı əməliyyatların sayını yoxlamaq üçün
    long countByTehtelhesabSexs_Id(Long tehtelhesabSexsId);
}