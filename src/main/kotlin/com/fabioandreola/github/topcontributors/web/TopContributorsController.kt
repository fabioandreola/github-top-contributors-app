package com.fabioandreola.github.topcontributors.web

import com.fabioandreola.github.topcontributors.api.TopContributorsResponse
import com.fabioandreola.github.topcontributors.service.GithubService
import com.fabioandreola.github.topcontributors.web.mapper.InvalidLocationException
import com.fabioandreola.github.topcontributors.web.mapper.InvalidMaxResults
import com.fabioandreola.github.topcontributors.web.mapper.toDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class TopContributorsController(private val githubService: GithubService) {

    companion object {
        const val MAX_RESULTS = 500
    }

    @GetMapping("/top-contributors")
    @Operation(summary = "Returns all the top github contributors in a location")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "400", description = "Bad request"),
        ApiResponse(responseCode = "401", description = "Not authenticated"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    @Parameter(name = "\$completion", `in` = ParameterIn.QUERY, hidden = true)
    suspend fun getTopContributors(@RequestParam(defaultValue = "50") maxResults: Int,
                                   @RequestParam(defaultValue = "") location: String): TopContributorsResponse {

        validateRequest(maxResults, location)
        val users = githubService.getTopContributors(location, maxResults)
                .asSequence()
                .map { user -> user.toDto() }
                .toList()
        return TopContributorsResponse(users)
    }

    private fun validateRequest(maxResults: Int, location: String) {
        if (location.isEmpty()) {
            throw InvalidLocationException("Must choose a location")
        }
        if (maxResults > MAX_RESULTS) {
            throw InvalidMaxResults("Max contributors must be <= $MAX_RESULTS")
        }
    }
}
