package eu.wswieciejutra.repo_checker.adapter;

import eu.wswieciejutra.repo_checker.repository.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import eu.wswieciejutra.repo_checker.repository.Branch;
import eu.wswieciejutra.repo_checker.repository.BranchInterface;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SqlBranchAdapter extends BranchInterface, JpaRepository<Branch, Long> {

    @Query("SELECT b FROM Branch b WHERE b.repository = :repository")
    List<Branch> findAllBranchesByRepository(@Param("repository") Repository repository);
}
