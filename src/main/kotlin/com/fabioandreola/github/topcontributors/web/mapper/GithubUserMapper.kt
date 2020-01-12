package com.fabioandreola.github.topcontributors.web.mapper

import com.fabioandreola.github.topcontributors.api.GitHubUserDto
import com.fabioandreola.github.topcontributors.model.GitHubUser

fun GitHubUser.toDto() = GitHubUserDto(this.login, this.location, this.url, this.publicRepositoryCount, this.avatarUrl)
