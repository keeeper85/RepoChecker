package eu.wswieciejutra.repo_checker.controller;

import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.GitHubService;
import eu.wswieciejutra.repo_checker.service.dto.BranchDto;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ViewController {

    private final GitHubService gitHubService;

    @Autowired
    public ViewController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @PostMapping("/search")
    public String search(@RequestParam("username") String username,
                         @RequestParam(name = "token", required = false) String token) {
        try {
            List<RepositoryDto> repositories = gitHubService.getNonForkRepositories(username, token);
            StringBuilder htmlResponse = new StringBuilder();
            htmlResponse.append("<div class=\"container\">");
            htmlResponse.append("<h1>Repositories of <span>").append(username).append("</span></h1>");
            htmlResponse.append("<div>");
            htmlResponse.append("<ul>");
            for (RepositoryDto repo : repositories) {
                htmlResponse.append("<li class=\"repository\">");
                htmlResponse.append("<p><strong>Name:</strong> <span>").append(repo.getName()).append("</span></p>");
                htmlResponse.append("<p><strong>Owner:</strong> <span>").append(repo.getOwner()).append("</span></p>");
                if (repo.getBranches() != null && !repo.getBranches().isEmpty()) {
                    htmlResponse.append("<div class=\"branches\">");
                    htmlResponse.append("<p><strong>Branches:</strong></p>");
                    htmlResponse.append("<ul>");
                    for (BranchDto branch : repo.getBranches()) {
                        htmlResponse.append("<li>");
                        htmlResponse.append("<p><span>").append(branch.getName()).append("</span> - <span>")
                                .append(branch.getLastCommitSha()).append("</span></p>");
                        htmlResponse.append("</li>");
                    }
                    htmlResponse.append("</ul>");
                    htmlResponse.append("</div>");
                }
                htmlResponse.append("</li>");
            }
            htmlResponse.append("</ul>");
            htmlResponse.append("</div>");
            htmlResponse.append("</div>");
            return htmlResponse.toString();
        } catch (UserNotFoundException e) {
            return "<div id=\"error\" style=\"color:red;\">User not found</div>";
        } catch (Exception e) {
            return "<div id=\"error\" style=\"color:red;\">An error occurred: " + e.getMessage() + "</div>";
        }
    }
}
