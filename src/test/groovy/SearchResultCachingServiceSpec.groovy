

import eu.wswieciejutra.repo_checker.InMemoryBranchRepository
import eu.wswieciejutra.repo_checker.InMemoryRepository
import eu.wswieciejutra.repo_checker.repository.*
import eu.wswieciejutra.repo_checker.service.SearchResultCachingService
import eu.wswieciejutra.repo_checker.service.dto.BranchDto
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto
import spock.lang.Specification
import spock.lang.Unroll

class SearchResultCachingServiceSpec extends Specification {

    RepositoryInterface repositoryInterface = new InMemoryRepository()
    BranchInterface branchInterface = new InMemoryBranchRepository()
    SearchResultCachingService searchResultCachingService = new SearchResultCachingService(repositoryInterface, branchInterface)
    String ownerLogin = "testuser"

    @Unroll
    def "should cache repositories and branches"() {
        given:
        def repositoryDto1 = new RepositoryDto("repo1", ownerLogin, false)
        def repositoryDto2 = new RepositoryDto("repo2", ownerLogin, false)

        def branchDto1 = new BranchDto("main", "abc123")
        def branchDto2 = new BranchDto("develop", "def456")
        repositoryDto1.setBranches([branchDto1, branchDto2])

        def repositories = [repositoryDto1, repositoryDto2]

        when:
        searchResultCachingService.cacheRepositories(repositories)

        then:
        def hasRepositoryBeenCached = repositoryInterface.existsByOwnerLogin(ownerLogin)
        hasRepositoryBeenCached
        def cachedRepositories = repositoryInterface.findAllByOwnerLogin(ownerLogin)
        cachedRepositories.size() == 2
        def cachedBranchesRepo1 = repositoryInterface.findAllByOwnerLogin(ownerLogin)
        cachedBranchesRepo1.size() == 2
        cachedBranchesRepo1.getFirst().name == "repo1"
        cachedBranchesRepo1.getFirst().getBranches().size() == 2
    }

    def "should check if repository has been cached"() {
        when:
        def cached = searchResultCachingService.hasRepositoryBeenCached(ownerLogin)

        then:
        !cached

        when:
        def repositoryDto = new RepositoryDto(name: "repo1", owner: ownerLogin, isFork: false)
        searchResultCachingService.cacheRepositories([repositoryDto])

        then:
        searchResultCachingService.hasRepositoryBeenCached(ownerLogin)
    }

    def "should return cached repositories"() {
        given:
        def repositoryDto1 = new RepositoryDto(name: "repo1", owner: ownerLogin, isFork: false)
        def repositoryDto2 = new RepositoryDto(name: "repo2", owner: ownerLogin, isFork: false)

        def repositories = [repositoryDto1, repositoryDto2]

        when:
        searchResultCachingService.cacheRepositories(repositories)
        def cachedRepositories = searchResultCachingService.getCachedRepositories(ownerLogin)

        then:
        cachedRepositories.size() == 2
        cachedRepositories.any { it.name == "repo1" }
        cachedRepositories.any { it.name == "repo2" }
    }

    @Unroll
    def "should return cached branches for a repository"() {
        given:
        def repositoryDto = new RepositoryDto("repo1", ownerLogin, false)
        def branchDto1 = new BranchDto("main", "abc123")
        def branchDto2 = new BranchDto("develop", "def456")
        repositoryDto.setBranches([branchDto1, branchDto2])

        when:
        searchResultCachingService.cacheRepositories([repositoryDto])
        def cachedRepositories = searchResultCachingService.getCachedRepositories(ownerLogin)

        then:
        cachedRepositories.size() == 1

    }
}
