package com.fabioandreola.github.topcontributors.gateway.github

import com.fabioandreola.github.topcontributors.model.GitHubUser

interface GithubApi {
    suspend fun getTopContributors(location: String, maxResults: Int, accessToken: String): List<GitHubUser>
}



