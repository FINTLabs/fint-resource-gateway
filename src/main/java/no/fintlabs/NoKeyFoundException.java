package no.fintlabs;

public class NoKeyFoundException extends Exception {
    public NoKeyFoundException() {
    }

    public NoKeyFoundException(String message) {
        super(message);
    }
}
