import eu.wswieciejutra.repo_checker.service.GitHubService
import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import eu.wswieciejutra.repo_checker.repository.Branch
import eu.wswieciejutra.repo_checker.repository.Repository
import spock.lang.Specification
import spock.lang.Unroll

class GitHubServiceSpec extends Specification {

    RestTemplate restTemplate = Mock(RestTemplate)
    GitHubService gitHubService = new GitHubService(restTemplate)

    @Unroll
    def "should return non-fork repositories"() {
        given:
        def username = "testuser"

        def repository1 = new Repository(name: "repo1", owner: new Repository.Owner(login: "testuser"), isFork: false)
        def repository2 = new Repository(name: "repo2", owner: new Repository.Owner(login: "testuser"), isFork: true)

        def repositories = [repository1, repository2] as Repository[]
        def repositoriesResponse = new ResponseEntity<>(repositories, HttpStatus.OK)

        def branch1 = new Branch(name: "main", commit: new Branch.Commit(sha: "abc123"))
        def branch2 = new Branch(name: "develop", commit: new Branch.Commit(sha: "def456"))
        def branches = [branch1, branch2] as Branch[]
        def branchesResponse = new ResponseEntity<>(branches, HttpStatus.OK)

        restTemplate.getForEntity(_ as String, Repository[].class) >> repositoriesResponse
        restTemplate.getForEntity(_ as String, Branch[].class) >> branchesResponse

        when:
        def result = gitHubService.getNonForkRepositories(username)

        then:
        result.size() == 1
        result[0].name == "repo1"
        result[0].branches.size() == 2
        result[0].branches[0].name == "main"
    }

    def "should throw UserNotFoundException when user does not exist"() {
        given:
        def username = "invaliduser"

        restTemplate.getForEntity(_ as String, Repository[].class) >> {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND)
        }

        when:
        gitHubService.getNonForkRepositories(username)

        then:
        thrown(UserNotFoundException)
    }


}
