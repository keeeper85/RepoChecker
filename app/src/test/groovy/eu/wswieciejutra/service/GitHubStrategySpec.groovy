package eu.wswieciejutra.service

import eu.wswieciejutra.Branch
import eu.wswieciejutra.Repository
import eu.wswieciejutra.exception.UserNotFoundException
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import spock.lang.Specification
import spock.lang.Unroll

class GitHubStrategySpec extends Specification {

    RestTemplate restTemplate = Mock(RestTemplate)
    GitHubStrategy gitHubService = new GitHubStrategy(restTemplate)

    @Unroll
    def "should return non-fork repositories"() {
        given:
        def username = "testuser"
        def token = "token"

        def repository1 = new Repository(name: "repo1", owner: new Repository.Owner(login: "testuser"), fork: false)
        def repository2 = new Repository(name: "repo2", owner: new Repository.Owner(login: "testuser"), fork: true)

        def repositories = [repository1, repository2] as Repository[]
        def repositoriesResponse = new ResponseEntity<>(repositories, HttpStatus.OK)

        def branch1 = new Branch(name: "main", commit: new Branch.Commit(sha: "abc123"))
        def branch2 = new Branch(name: "develop", commit: new Branch.Commit(sha: "def456"))
        def branches = [branch1, branch2] as Branch[]
        def branchesResponse = new ResponseEntity<>(branches, HttpStatus.OK)

        restTemplate.exchange("https://api.github.com/users/testuser/repos", HttpMethod.GET, spock.lang.Specification._ as HttpEntity, Repository[].class) >> repositoriesResponse
        restTemplate.exchange("https://api.github.com/repos/testuser/repo1/branches", HttpMethod.GET, spock.lang.Specification._ as HttpEntity, Branch[].class) >> branchesResponse

        when:
        def result = gitHubService.getNonForkRepositories(username, token)

        then:
        result.size() == 1
        result[0].name == "repo1"
        result[0].branches.size() == 2
        result[0].branches[0].name == "main"
        result[0].branches[1].name == "develop"
    }

    def "should throw UserNotFoundException when user does not exist"() {
        given:
        def username = "invaliduser"
        def token = "token"

        restTemplate.exchange(spock.lang.Specification._ as String, HttpMethod.GET, spock.lang.Specification._ as HttpEntity, Repository[].class) >> {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND)
        }

        when:
        gitHubService.getNonForkRepositories(username, token)

        then:
        thrown(UserNotFoundException.class)
    }
}
