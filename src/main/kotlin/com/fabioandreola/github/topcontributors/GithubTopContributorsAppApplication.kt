package com.fabioandreola.github.topcontributors

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GithubTopContributorsAppApplication

fun main(args: Array<String>) {
    runApplication<GithubTopContributorsAppApplication>(*args)
}
