package com.fabioandreola.github.topcontributors.service

import com.fabioandreola.github.topcontributors.gateway.github.GithubApi
import com.fabioandreola.github.topcontributors.model.GitHubUser
import org.springframework.stereotype.Service

@Service
class GithubService(private val securityService: SecurityService,
                    private val githubApi: GithubApi) {

    suspend fun getTopContributors(location: String, maxResults: Int): List<GitHubUser> {
        val oauthToken = securityService.getOauthToken() ?: error("Token not found")
        return githubApi.getTopContributors(location, maxResults, oauthToken)
    }
}
