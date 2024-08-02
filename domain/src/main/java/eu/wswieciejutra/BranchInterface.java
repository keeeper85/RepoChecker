package eu.wswieciejutra;

import java.util.List;

public interface BranchInterface {

    List<Branch> findAllBranchesByRepository(Repository repository);
}
