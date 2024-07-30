package eu.wswieciejutra.repo_checker.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import eu.wswieciejutra.repo_checker.repository.Branch;
import eu.wswieciejutra.repo_checker.repository.BranchInterface;

public interface SqlBranchAdapter extends BranchInterface, JpaRepository<Branch, Long> {
}
