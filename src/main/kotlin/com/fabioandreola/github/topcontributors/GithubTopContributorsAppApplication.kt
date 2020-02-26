package com.fabioandreola.github.topcontributors

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession

@SpringBootApplication
@EnableRedisWebSession
class GithubTopContributorsAppApplication

fun main(args: Array<String>) {
    runApplication<GithubTopContributorsAppApplication>(*args)
}
