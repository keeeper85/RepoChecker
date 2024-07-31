package eu.wswieciejutra.repo_checker.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public record BranchDto(String name, String lastCommitSha) {

}
