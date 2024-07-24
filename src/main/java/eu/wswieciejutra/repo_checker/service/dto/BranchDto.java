package eu.wswieciejutra.repo_checker.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchDto {

    private String name;
    private String lastCommitSha;
}
