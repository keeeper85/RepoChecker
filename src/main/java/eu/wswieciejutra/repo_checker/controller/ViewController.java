package eu.wswieciejutra.repo_checker.controller;

import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.Facade;
import eu.wswieciejutra.repo_checker.service.dto.BranchDto;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "View Controller", description = "Search for GitHub/GitLab users with GUI (used by HTML/HTMX View files)")
public class ViewController {

    private final Facade facade;

    @Autowired
    public ViewController(Facade facade) {
        this.facade = facade;
    }


    @Operation(description = "Get HTML-preformatted list of user's repositories")
    @PostMapping("/search")
    public String search(@RequestParam("service") String service,
                         @RequestParam("username") String username,
                         @RequestParam(name = "token", required = false) String token) {
        try {
            List<RepositoryDto> repositories = facade.getNonForkRepositories(service, username, token);
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
                        htmlResponse.append("<p><span>").append(branch.name()).append("</span> - <span>")
                                .append(branch.lastCommitSha()).append("</span></p>");
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
