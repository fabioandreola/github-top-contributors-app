package com.fabioandreola.github.topcontributors.model

data class GitHubUser(
        val login: String,
        val location: String?,
        val url: String,
        val avatarUrl: String,
        val publicRepositoryCount: Int)
