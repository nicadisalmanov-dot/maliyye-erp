package az.maliyye.MaliyyeApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlisdanGeriQaytarmaRepository extends JpaRepository<AlisdanGeriQaytarma, Long> {
    AlisdanGeriQaytarma findTopByOrderByIdDesc();
    List<AlisdanGeriQaytarma> findBySilinibFalseOrderByIdDesc();
    List<AlisdanGeriQaytarma> findBySilinibTrueOrderByIdDesc();
}