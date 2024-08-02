package eu.wswieciejutra.service;


import eu.wswieciejutra.dto.BranchDto;
import eu.wswieciejutra.dto.RepositoryDto;
import eu.wswieciejutra.exception.ApiLimitReachedException;
import eu.wswieciejutra.exception.UserNotFoundException;

import java.util.List;

public interface ServiceStrategyInterface {
    List<RepositoryDto> getNonForkRepositories(String username, String token) throws UserNotFoundException, ApiLimitReachedException;
    List<BranchDto> fetchBranchesForRepository(String owner, String repoName, String token);
}
