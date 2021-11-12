package no.fintlabs.fint;

import org.springframework.util.StringUtils;

public class FintTopicUtilities {

    public static <T> String getTopicNameFromEndpointUrl(String endpoint) {
        return "entity" + endpoint.replace("/", ".");
    }
}
