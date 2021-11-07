package no.fintlabs.fint;

public class FintTopicUtilities {

    public static <T> String getTopicNameFromFintClassResource(Class<T> clazz) throws NotAFintClassResource {
        String canonicalName = clazz.getCanonicalName();

        if (canonicalName.contains("no.fint.model")) {
            return canonicalName.replace("no.fint.model.", "").toLowerCase();
        }

        throw new NotAFintClassResource(canonicalName + " is not a FINT class");
    }
}
