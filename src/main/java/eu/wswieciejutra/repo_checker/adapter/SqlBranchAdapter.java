package eu.wswieciejutra.repo_checker.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import repository.Branch;
import repository.BranchInterface;

public interface SqlBranchAdapter extends BranchInterface, JpaRepository<Branch, Long> {
}
