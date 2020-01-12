package com.fabioandreola.github.topcontributors.web


import com.fabioandreola.github.topcontributors.service.GithubService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

import static com.fabioandreola.github.topcontributors.web.TopContributorsController.MAX_RESULTS
import static org.springframework.http.MediaType.APPLICATION_JSON

@WebFluxTest(TopContributorsController)
@WithMockUser
class TopContributorsControllerTest extends Specification {

    private static final String TOP_CONTRIBUTORS_URI = "/api/top-contributors"
    private static final int DEFAULT_MAX_CONTRIBUTORS = 50

    @SpringBean
    GithubService githubService = Mock()

    @Autowired
    private WebTestClient webTestClient

    def "should return error if location is not provided"() {
        given: "we don't provide a location"
        def uri = "$TOP_CONTRIBUTORS_URI?location=&maxResults=100"

        when: "we try to fetch the top contributors"
        def response = webTestClient.get()
                .uri(uri)
                .accept(APPLICATION_JSON)
                .exchange()

        then: "we should get a bad request error back"
        response.expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("isError").isEqualTo(true)
                .jsonPath("errorMessage").isEqualTo("Must choose a location")
    }

    def "should return error if max results greater than permitted"() {
        given: "we try to fetch more results than the max permitted"
        def uri = "$TOP_CONTRIBUTORS_URI?location=barcelona&maxResults=${MAX_RESULTS + 1}"

        when: "we try to fetch the top contributors"
        def response = webTestClient.get()
                .uri(uri)
                .accept(APPLICATION_JSON)
                .exchange()

        then: "we should get a bad request error back"
        response.expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("isError").isEqualTo(true)
                .jsonPath("errorMessage").isEqualTo("Max contributors must be <= 500")
    }

    def "should use default max results if one is not provided"() {
        given: "we don't provide the maxResults parameter"
        def uri = "$TOP_CONTRIBUTORS_URI?location=barcelona&maxResults="

        and: "the github service is working"
        def maxContributorsArg = 0
        githubService.getTopContributors(*_) >> { args -> maxContributorsArg = args[1]; [] }

        when: "we try to fetch the top contributors"
        def response = webTestClient.get()
                .uri(uri)
                .accept(APPLICATION_JSON)
                .exchange()

        then: "we should succeed"
        response.expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("isError").isEqualTo(false)

        and: "the github service received the default value"
        maxContributorsArg == DEFAULT_MAX_CONTRIBUTORS
    }

    @WithAnonymousUser
    def "Anonymous users should have access denied"() {
        expect:
        webTestClient.get()
                .uri(TOP_CONTRIBUTORS_URI)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden()
    }
}
