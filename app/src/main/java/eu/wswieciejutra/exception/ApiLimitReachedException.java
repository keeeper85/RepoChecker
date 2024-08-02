package eu.wswieciejutra.exception;

public class ApiLimitReachedException extends Exception {

    public ApiLimitReachedException(String message) {
        super(message);
    }
}
