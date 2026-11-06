package az.maliyye.MaliyyeApp.smeta;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SmetaRepository extends JpaRepository<Smeta, Long> {

    // Həm smeta bəndlərini, həm də Layihəni (layihe) birgə yükləyirik
    @EntityGraph(attributePaths = {"bəndlər", "layihe"})
    List<Smeta> findAll();

    @EntityGraph(attributePaths = {"bəndlər", "layihe"})
    List<Smeta> findByLayiheId(Long layiheId);

    @Override
    @EntityGraph(attributePaths = {"bəndlər", "layihe"})
    Optional<Smeta> findById(Long id);
}