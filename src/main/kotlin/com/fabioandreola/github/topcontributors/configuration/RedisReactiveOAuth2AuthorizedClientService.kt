package com.fabioandreola.github.topcontributors.configuration

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.util.Assert
import reactor.core.publisher.Mono
import java.time.Duration


class RedisReactiveOAuth2AuthorizedClientService(private val clientRegistrationRepository: ReactiveClientRegistrationRepository,
                                                 private val reactiveOauthRedisTemplate: ReactiveRedisTemplate<String, Any>,
                                                 private val appName: String,
                                                 private val sessionTimeout: Duration) : ReactiveOAuth2AuthorizedClientService {
    companion object {
        private const val DEFAULT_OAUTH2_AUTHORIZED_CLIENT_KEY_PREFIX = "OAUTH2_AUTHORIZED_CLIENT"
    }

    override fun <T : OAuth2AuthorizedClient> loadAuthorizedClient(clientRegistrationId: String,
                                                                   principalName: String): Mono<T> {
        Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty")
        Assert.hasText(principalName, "principalName cannot be empty")
        return clientRegistrationRepository.findByRegistrationId(clientRegistrationId)
                .map { oauth2AuthorizedClientKey(clientRegistrationId, principalName) }
                .flatMap { key: String ->
                    reactiveOauthRedisTemplate.opsForValue().get(key) as Mono<T>
                }
    }

    override fun saveAuthorizedClient(authorizedClient: OAuth2AuthorizedClient, principal: Authentication): Mono<Void> {
        Assert.notNull(authorizedClient, "authorizedClient cannot be null")
        Assert.notNull(principal, "principal cannot be null")
        val key = oauth2AuthorizedClientKey(authorizedClient.clientRegistration.registrationId, principal.name)
        return reactiveOauthRedisTemplate.opsForValue().set(key, authorizedClient, sessionTimeout)
                .then(Mono.empty())
    }

    override fun removeAuthorizedClient(clientRegistrationId: String, principalName: String): Mono<Void> {
        Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty")
        Assert.hasText(principalName, "principalName cannot be empty")
        return clientRegistrationRepository.findByRegistrationId(clientRegistrationId)
                .map { oauth2AuthorizedClientKey(clientRegistrationId, principalName) }
                .doOnNext { key: String -> reactiveOauthRedisTemplate.delete(key) }
                .then(Mono.empty())
    }

    private fun oauth2AuthorizedClientKey(clientRegistrationId: String, principalName: String): String {
        return "${appName}_${DEFAULT_OAUTH2_AUTHORIZED_CLIENT_KEY_PREFIX}_${clientRegistrationId}_${principalName}"
    }
}
