package no.fintlabs.fint;

import no.fint.model.resource.FintLinks;

public class LinkUtilities {

    public static String getSystemIdSelfLink(FintLinks o) {
        return o.getSelfLinks()
                .stream()
                .filter(link -> link.getHref().toLowerCase().contains("systemid"))
                .findAny()
                .orElseThrow()
                .getHref();
    }
}
