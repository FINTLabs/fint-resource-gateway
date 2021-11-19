package no.fintlabs.kafka;

import org.springframework.util.StringUtils;

public class FintTopicUtilities {

    public static String getEntityTopicNameFromEndpointUrl(String endpoint) {
        return "entity" + endpoint.replace("/", ".");
    }

    public static String getRequestTopicNameFromEndpointUrl(String endpoint, String suffix) {
        return "request" + endpoint.replace("/", ".") + "." + suffix;
    }
}
