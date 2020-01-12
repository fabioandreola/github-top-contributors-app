package com.fabioandreola.github.topcontributors.api

data class TopContributorsResponse(val users: List<GitHubUserDto>, val isError: Boolean = false, val errorMessage: String? = null)
