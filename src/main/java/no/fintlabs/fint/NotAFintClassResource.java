package no.fintlabs.fint;

public class NotAFintClassResource extends RuntimeException {
    public NotAFintClassResource() {
    }

    public NotAFintClassResource(String message) {
        super(message);
    }
}
