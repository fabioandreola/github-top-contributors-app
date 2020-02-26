package com.fabioandreola.github.topcontributors

import com.fabioandreola.github.topcontributors.configuration.RedisConfiguration
import com.fabioandreola.github.topcontributors.gateway.github.GraphQlGithubApi
import com.fabioandreola.github.topcontributors.service.GithubService
import com.fabioandreola.github.topcontributors.service.SecurityService
import com.fabioandreola.github.topcontributors.web.TopContributorsController
import com.github.tomakehurst.wiremock.client.WireMock
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient
import spock.lang.Specification

import static com.fabioandreola.github.topcontributors.util.TestUtil.getFileContent
import static com.github.tomakehurst.wiremock.client.WireMock.okJson
import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static org.springframework.http.MediaType.APPLICATION_JSON


@WebFluxTest(TopContributorsController)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
@Import([GraphQlGithubApi, GithubService, RedisReactiveAutoConfiguration, RedisConfiguration, RedisAutoConfiguration])
@WithMockUser
class TopContributorsIntegrationTest extends Specification {

    private static final String TOP_CONTRIBUTORS_URI = "/api/top-contributors"

    private WireMock wiremock

    @Value("\${wiremock.server.port}")
    private int wiremockPort

    @Autowired
    private WebTestClient webTestClient

    @SpringBean
    SecurityService securityService = Mock()

    @SpringBean
    WebClient webClient = WebClient.create()

    def setup() {
        wiremock = new WireMock("localhost", wiremockPort)
        wiremock.resetRequests()
        securityService.getOauthToken(*_) >> "MOCK_TOKEN"
        wiremock.stubFor(post(urlEqualTo("/")).willReturn(okJson(getFileContent("graphql-top-contributors-mock-response.json"))))
    }

    def "should fetch the top contributors from github"() {
        given: "we have valid parameters in the url"
        def uri = "$TOP_CONTRIBUTORS_URI?location=barcelona&maxResults=50"

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
                .jsonPath("\$.users[0].username").isEqualTo("nilportugues")
    }

    def "should chunk calls to github if trying to fetch more than the github limit of 100"() {
        given: "we are trying to fetch 350 top contributors"
        def uri = "$TOP_CONTRIBUTORS_URI?location=barcelona&maxResults=350"

        when: "we try to fetch the top contributors"
        def response = webTestClient.get()
                .uri(uri)
                .accept(APPLICATION_JSON)
                .exchange()

        then: "we should succeed"
        response.expectStatus().isOk()

        and: "we chunked the calls to github"
        // 3 calls of 100 and one of 50
        wiremock.verifyThat(4, postRequestedFor(urlEqualTo("/")))
    }
}

