package com.fabioandreola.github.topcontributors.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.router
import java.util.ArrayList

@Configuration
class ApplicationConfiguration {

    @Value("classpath:/static/index.html")
    private lateinit var indexHtml: Resource

    @Bean
    fun mainRouter() = router {
        GET("/") {
            ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml)
        }
    }

    @Bean
    fun webclient(): WebClient = WebClient.create()

    @Bean
    fun clientRegistrationRepository(properties: OAuth2ClientProperties?): InMemoryReactiveClientRegistrationRepository? {
        val registrations: List<ClientRegistration> = ArrayList(
                OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties).values)
        return InMemoryReactiveClientRegistrationRepository(registrations)
    }

    @Bean
    fun authorizedClientService(
            clientRegistrationRepository: ReactiveClientRegistrationRepository?): ReactiveOAuth2AuthorizedClientService? {
        return InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository)
    }
}
