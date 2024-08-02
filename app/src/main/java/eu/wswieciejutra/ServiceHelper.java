package eu.wswieciejutra;

import eu.wswieciejutra.dto.BranchDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServiceHelper {

    public static void tokenCheckLogging(String token) {
        if (token != null && !token.isEmpty()) {
            LoggerUtility.LOGGER.info("Using token for authentication");
        } else {
            LoggerUtility.LOGGER.info("No token provided");
        }
    }

    public static List<BranchDto> getBranches(RestTemplate restTemplate, String url, String token) {
        HttpEntity<String> entity = Factory.createHttpEntity(token);
        ServiceHelper.tokenCheckLogging(token);

        Optional<ResponseEntity<Branch[]>> response = getResponse(restTemplate, url, entity);
        if (response.isEmpty()) return Collections.emptyList();

        Branch[] branches = response.get().getBody();

        return (branches != null) ? Arrays.stream(branches)
                .map(Factory::convertToBranchDto)
                .collect(Collectors.toList())
                : Collections.emptyList();
    }

    public static Optional<ResponseEntity<Branch[]>> getResponse(RestTemplate restTemplate, String url, HttpEntity<String> entity) {
        ResponseEntity<Branch[]> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, Branch[].class);
            if (response.getBody() == null) {
                return Optional.empty();
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw e;
            }
        }
        return Optional.of(response);
    }
}
