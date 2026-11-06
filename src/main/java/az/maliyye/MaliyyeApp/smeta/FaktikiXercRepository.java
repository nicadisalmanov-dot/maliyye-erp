package az.maliyye.MaliyyeApp.smeta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaktikiXercRepository extends JpaRepository<FaktikiXerc, Long> {
    List<FaktikiXerc> findBySmetaBendiId(Long smetaBendiId);

    @Query("SELECT SUM(f.mebleg) FROM FaktikiXerc f WHERE f.smetaBendi.id = :bendiId")
    Double getCemiFaktikiXercByBendiId(@Param("bendiId") Long bendiId);
}