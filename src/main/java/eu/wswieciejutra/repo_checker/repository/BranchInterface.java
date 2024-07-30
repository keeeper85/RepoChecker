package eu.wswieciejutra.repo_checker.repository;

import java.util.List;

public interface BranchInterface {

    List<Branch> findAllBranchesByRepository(Repository repository);
    Branch save(Branch branch);
}
