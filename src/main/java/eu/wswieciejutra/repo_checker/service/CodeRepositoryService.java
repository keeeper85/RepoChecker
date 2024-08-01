package eu.wswieciejutra.repo_checker.service;

import eu.wswieciejutra.repo_checker.exception.ApiLimitReachedException;
import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.dto.BranchDto;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;

import java.util.List;

public interface CodeRepositoryService {
    List<RepositoryDto> getNonForkRepositories(String username, String token) throws UserNotFoundException, ApiLimitReachedException;
    List<BranchDto> fetchBranchesForRepository(String owner, String repoName, String token);
}
