package no.fintlabs.kafka

import no.fint.model.arkiv.kodeverk.Rolle
import no.fintlabs.fint.NotAFintClassResource
import no.fintlabs.fint.FintTopicUtilities
import spock.lang.Specification

class FintTopicUtilitiesSpec extends Specification {

    def "Getting topic name from a FINT class should return the canonical name minus no.fint.model"() {

        when:
        def topicName = FintTopicUtilities.getTopicNameFromFintClassResource(Rolle.class)

        then:
        topicName == "arkiv.kodeverk.rolle"
        noExceptionThrown()
    }

    def "If the class is not a FINT class an exception should be thrown"() {

        when:
        FintTopicUtilities.getTopicNameFromFintClassResource(String.class)

        then:
        def e = thrown(NotAFintClassResource)
        e.getMessage().startsWith("java.lang.String")
    }
}
