package eu.wswieciejutra.repo_checker.exception;

public class ApiLimitReachedException extends Exception {

    public ApiLimitReachedException(String message) {
        super(message);
    }
}
