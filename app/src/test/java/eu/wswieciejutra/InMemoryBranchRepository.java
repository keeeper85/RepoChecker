package eu.wswieciejutra;

import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryBranchRepository implements BranchInterface {

    private final Map<Long, Branch> branchMap = new ConcurrentHashMap<>();
    private long currentId = 1;

    @Override
    public List<Branch> findAllBranchesByRepository(Repository repository) {
        List<Branch> branches = new ArrayList<>();
        for (Branch branch : branchMap.values()) {
            if (branch.getRepository().getId().equals(repository.getId())) {
                branches.add(branch);
            }
        }
        return branches;
    }

    public Branch save(Branch branch) {
        if (branch.getId() == null) {
            branch.setId(currentId++);
        }
        branchMap.put(branch.getId(), branch);
        return branch;
    }
}
