package com.fabioandreola.github.topcontributors.service

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service


@Service
class SecurityService(private val authorizedClientService: ReactiveOAuth2AuthorizedClientService) {
    suspend fun getOauthToken(): String? {
        val oAuth2Token: OAuth2AuthenticationToken = ReactiveSecurityContextHolder.getContext()
                .map { it.authentication as OAuth2AuthenticationToken }
                .awaitSingle()

        val client = authorizedClientService.loadAuthorizedClient<OAuth2AuthorizedClient>(
                oAuth2Token.authorizedClientRegistrationId,
                oAuth2Token.name)
                .awaitSingle()

        return client.accessToken.tokenValue
    }
}
