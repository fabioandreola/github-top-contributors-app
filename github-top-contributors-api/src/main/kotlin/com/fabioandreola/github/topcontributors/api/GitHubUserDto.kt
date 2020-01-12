package com.fabioandreola.github.topcontributors.api

data class GitHubUserDto(
        val username: String,
        val location: String?,
        val url: String,
        val publicRepositoryCount: Int,
        val avatarUrl: String)
