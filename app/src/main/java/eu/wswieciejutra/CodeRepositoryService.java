package eu.wswieciejutra;


import eu.wswieciejutra.dto.BranchDto;
import eu.wswieciejutra.dto.RepositoryDto;
import eu.wswieciejutra.exception.ApiLimitReachedException;
import eu.wswieciejutra.exception.UserNotFoundException;

import java.util.List;

public interface CodeRepositoryService {
    List<RepositoryDto> getNonForkRepositories(String username, String token) throws UserNotFoundException, ApiLimitReachedException;
    List<BranchDto> fetchBranchesForRepository(String owner, String repoName, String token);
}
