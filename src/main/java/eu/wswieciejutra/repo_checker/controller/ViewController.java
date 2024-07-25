package eu.wswieciejutra.repo_checker.controller;

import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.GitHubService;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ViewController {

    private final GitHubService gitHubService;

    public ViewController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/search")
    public String search(@RequestParam("username") String username, Model model) {
        try {
            List<RepositoryDto> repositories = gitHubService.getNonForkRepositories(username);
            model.addAttribute("repositories", repositories);
            model.addAttribute("username", username);
            return "result";
        } catch (UserNotFoundException e) {
            model.addAttribute("error", "User not found");
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "index";
        }
    }

}
