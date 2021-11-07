package no.fintlabs

import no.fint.model.resource.Link
import no.fint.model.resource.arkiv.noark.AdministrativEnhetResource
import no.fintlabs.fint.LinkUtilities
import spock.lang.Specification

class LinkUtilitiesSpec extends Specification {

    def "Get systemid self link should return systemid self link"() {
        given:
        def resource = new AdministrativEnhetResource()
        resource.addSelf(Link.with("https://domain.no/systemid/123"))
        resource.addSelf(Link.with("https://domain.no/orgnr/321"))

        when:
        def id = LinkUtilities.getSystemIdSelfLink(resource)

        then:
        id == "https://domain.no/systemid/123"
        noExceptionThrown()
    }

    def "If no systemid self link exists an NoSuchElementException should be thrown"() {
        given:
        def resource = new AdministrativEnhetResource()
        resource.addSelf(Link.with("https://domain.no/orgnr/321"))

        when:
        LinkUtilities.getSystemIdSelfLink(resource)

        then:
        thrown(NoSuchElementException)
    }
}
