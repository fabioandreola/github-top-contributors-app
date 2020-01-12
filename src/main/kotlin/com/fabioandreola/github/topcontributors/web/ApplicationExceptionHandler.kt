package com.fabioandreola.github.topcontributors.web

import com.fabioandreola.github.topcontributors.api.TopContributorsResponse
import com.fabioandreola.github.topcontributors.web.mapper.InvalidLocationException
import com.fabioandreola.github.topcontributors.web.mapper.InvalidMaxResults
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ApplicationExceptionHandler {

    @ExceptionHandler(InvalidLocationException::class)
    suspend fun handle(ex: InvalidLocationException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(TopContributorsResponse(users = emptyList(), isError = true, errorMessage = ex.message))
    }

    @ExceptionHandler(InvalidMaxResults::class)
    suspend fun handle(ex: InvalidMaxResults): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(TopContributorsResponse(users = emptyList(), isError = true, errorMessage = ex.message))
    }

    @ExceptionHandler(Exception::class)
    suspend fun handle(ex: Exception): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(TopContributorsResponse(users = emptyList(), isError = true,
                        errorMessage = "Ops, this is embarrassing. Something really bad happened here-> ${ex.message}"))
    }
}
