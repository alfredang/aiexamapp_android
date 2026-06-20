package com.tertiaryinfotech.aiexams.data

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Raised for any API failure; [message] is surfaced directly to the user. */
class ApiException(message: String) : Exception(message)

/**
 * Thin wrapper around [ApiService] that injects the bearer token and converts
 * HTTP/parse errors into a friendly [ApiException], mirroring the iOS APIClient.
 */
class ApiClient(
    baseUrl: String = "https://exams.tertiaryinfotech.com/",
    private val tokenProvider: () -> String?,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val service: ApiService

    init {
        val http = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        service = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(http)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }

    private fun bearer(): String {
        val token = tokenProvider() ?: throw ApiException("Please sign in again.")
        return "Bearer $token"
    }

    private inline fun <T> call(block: () -> T): T = try {
        block()
    } catch (e: HttpException) {
        val raw = e.response()?.errorBody()?.string()
        val message = raw?.let {
            runCatching { json.decodeFromString<ServerError>(it).error }.getOrNull()
        } ?: "Server returned HTTP ${e.code()}."
        throw ApiException(message)
    } catch (e: IOException) {
        throw ApiException("Network error. Check your connection and try again.")
    } catch (e: ApiException) {
        throw e
    } catch (e: Exception) {
        throw ApiException(e.message ?: "Something went wrong.")
    }

    suspend fun login(email: String, password: String): AuthResponse =
        call { service.login(mapOf("email" to email, "password" to password)) }

    suspend fun register(name: String, email: String, password: String): AuthResponse =
        call { service.register(mapOf("name" to name, "email" to email, "password" to password)) }

    suspend fun catalog(query: String = "", vendor: String? = null): CatalogResponse =
        call { service.catalog(query.ifEmpty { null }, vendor) }

    suspend fun library(): LibraryResponse =
        call { service.library(bearer()) }

    suspend fun startAttempt(examId: String, mode: ExamMode, teaser: Boolean = false): StartAttemptResponse =
        call { service.startAttempt(bearer(), StartAttemptRequest(examId, mode.apiValue, teaser)) }

    suspend fun attempt(id: String): AttemptResponse =
        call { service.attempt(bearer(), id) }

    suspend fun answer(attemptId: String, questionId: String, answer: List<String>, flagged: Boolean?): AnswerResponse =
        call { service.answer(bearer(), AnswerRequest(attemptId, questionId, answer, flagged)) }

    suspend fun submit(attemptId: String): AttemptScore =
        call { service.submit(bearer(), SubmitRequest(attemptId)) }

    suspend fun deleteAccount(): DeleteAccountResponse =
        call { service.deleteAccount(bearer()) }
}
