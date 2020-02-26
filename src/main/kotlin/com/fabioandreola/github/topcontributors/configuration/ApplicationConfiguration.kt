package com.fabioandreola.github.topcontributors.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest
import org.springframework.boot.actuate.health.HealthEndpoint
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.MediaType
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.router
import java.time.Duration
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
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        http.authorizeExchange { exchanges: AuthorizeExchangeSpec ->
            exchanges.matchers(EndpointRequest.to(HealthEndpoint::class.java, InfoEndpoint::class.java)).permitAll()
            exchanges.anyExchange().authenticated()
        }
        http.oauth2Login()
        http.oauth2Client()
        return http.build()
    }

    @Bean
    fun webClient(clientRegistrationRepository: ReactiveClientRegistrationRepository,
                  authorizedClientRepository: ServerOAuth2AuthorizedClientRepository?): WebClient {
        val oauth = ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository, authorizedClientRepository)
        return WebClient.builder()
                .filter(oauth)
                .build()
    }


    @Bean
    fun clientRegistrationRepository(properties: OAuth2ClientProperties): InMemoryReactiveClientRegistrationRepository? {
        val registrations: List<ClientRegistration> = ArrayList(
                OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties).values)
        return InMemoryReactiveClientRegistrationRepository(registrations)
    }

    @Bean
    fun authorizedClientService(clientRegistrationRepository: ReactiveClientRegistrationRepository,
                                reactiveOauthRedisTemplate: ReactiveRedisTemplate<String, Any>,
                                @Value("\${spring.application.name}") appName: String,
                                @Value("\${spring.session.timeout}") sessionTimeout: Duration): ReactiveOAuth2AuthorizedClientService? {
        return RedisReactiveOAuth2AuthorizedClientService(clientRegistrationRepository, reactiveOauthRedisTemplate, appName, sessionTimeout)
    }
}
