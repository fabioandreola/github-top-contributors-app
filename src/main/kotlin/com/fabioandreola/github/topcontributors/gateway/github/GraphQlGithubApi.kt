package com.fabioandreola.github.topcontributors.gateway.github

import com.fabioandreola.github.topcontributors.model.GitHubUser
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange


@Component
/**
 * This a non-blocking implementation of the GithubApi that uses the Github GraphQl V4 api to retrieve a list of all users in a given location
 * ordered by the number of public repositories they have.
 * Because the Github api limits the number of results to 100 this class will actually make multiple calls until it fulfils the
 * maxResults requested.
 *
 * <p>Note: Some entries returned by Github are actually empty and therefore are filtered out from the results so requesting 100 might
 * actually return less entries even if Github says they have it due to the empty results.</p>
 */
class GraphQlGithubApi(private val webclient: WebClient,
                       @Value("\${application.github.graphql.url}") private val apiUrl: String,
                       private val objectMapper: ObjectMapper) : GithubApi {

    companion object {
        const val GITHUB_GRAPHQL_MAX_RESULTS = 100
    }

    override suspend fun getTopContributors(location: String, maxResults: Int, accessToken: String): List<GitHubUser> {
        val response = mutableListOf<GitHubUser>()
        //Github only returns a max of 100 entries per search so we need to chunk our requests.
        val maxResultsPerRequest: List<Int> = getMaxResultsPerRequest(maxResults)
        var fromPageCursor = ""

        maxResultsPerRequest.forEach { chunkSize ->
            val stringResult = webclient.post()
                    .uri(apiUrl)
                    .body(BodyInserters.fromValue(getTopContributorsGraphQlQuery(location, chunkSize, fromPageCursor)))
                    .header(AUTHORIZATION, "Bearer $accessToken")
                    .accept(APPLICATION_JSON)
                    .awaitExchange()
                    .awaitBody<String>()

            val githubResponse = objectMapper.readValue(stringResult, GraphQlResponse::class.java)
            response.addAll(mapResult(githubResponse))

            if (githubResponse.data.search.pageInfo.hasNextPage) {
                fromPageCursor = githubResponse.data.search.pageInfo.endCursor ?: ""
            } else {
                return response
            }
        }

        return response
    }

    /**
     * This function calculates the max results we can get per request to fetch all the data requested by the user.
     * <p>Example: If the user of the API wants 350 maxResults then we need to make 3 requests of 100 and one of 50.</p>
     */
    private fun getMaxResultsPerRequest(maxResults: Int): List<Int> {
        val chunks = mutableListOf<Int>()
        return if (maxResults <= GITHUB_GRAPHQL_MAX_RESULTS) {
            listOf(maxResults)
        } else {
            val chunksOfOneHundred = maxResults / GITHUB_GRAPHQL_MAX_RESULTS
            val finalChunk = maxResults % GITHUB_GRAPHQL_MAX_RESULTS
            repeat(chunksOfOneHundred) {
                chunks.add(GITHUB_GRAPHQL_MAX_RESULTS)
            }
            if (finalChunk != 0) {
                chunks.add(finalChunk)
            }
            chunks
        }
    }

    private fun mapResult(result: GraphQlResponse): List<GitHubUser> {
        val entries = result.data.search.edges ?: emptyList()
        return entries.asSequence()
                .filter { it.node?.login != null }
                .map {
                    val user = it.node!!
                    GitHubUser(user.login!!, user.location, user.url!!, user.avatarUrl!!, user.repositories?.totalCount!!)
                }
                .toList()
    }

    private fun getTopContributorsGraphQlQuery(location: String, maxResults: Int, after: String): String {
        var afterQuery = ""
        if (after.isNotBlank()) {
            afterQuery = ", after: \\\"$after\\\""
        }
        val query = "{\"query\":\"{search(first:$maxResults, query: \\\"location:$location sort:repositories-desc\\\", type: USER $afterQuery) " +
                "{ userCount edges{ node{ ... on User{ login location url avatarUrl repositories(ownerAffiliations: OWNER){ totalCount }}}} " +
                "pageInfo { hasNextPage endCursor }} }\",\"variables\":null}";
        return query
    }
}

// internal model
data class GraphQlResponse(val data: Data)

data class Data(val search: SearchResult)
data class SearchResult(val userCount: Int?, val edges: List<Node>?, val pageInfo: PageInfo)
data class Node(val node: UserResult?)
data class UserResult(val login: String?, val location: String?, val url: String?, val avatarUrl: String?, val repositories: Repository?)
data class Repository(val totalCount: Int)
data class PageInfo(val hasNextPage: Boolean, val endCursor: String?)
