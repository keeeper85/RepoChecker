package eu.wswieciejutra.persistence;

import eu.wswieciejutra.Branch;
import eu.wswieciejutra.BranchInterface;
import eu.wswieciejutra.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SqlBranchAdapter extends BranchInterface, JpaRepository<Branch, Long> {

    @Query("SELECT b FROM Branch b WHERE b.repository = :repository")
    List<Branch> findAllBranchesByRepository(@Param("repository") Repository repository);
}
