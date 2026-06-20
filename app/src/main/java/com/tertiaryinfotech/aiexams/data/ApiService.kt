package com.tertiaryinfotech.aiexams.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for the mobile API. Authorized endpoints take an explicit
 * Authorization header so the [ApiClient] can inject the current bearer token.
 */
interface ApiService {

    @POST("api/mobile/auth/login")
    suspend fun login(@Body body: Map<String, String>): AuthResponse

    @POST("api/mobile/auth/register")
    suspend fun register(@Body body: Map<String, String>): AuthResponse

    @GET("api/mobile/catalog")
    suspend fun catalog(
        @Query("q") query: String? = null,
        @Query("vendor") vendor: String? = null,
    ): CatalogResponse

    @GET("api/mobile/library")
    suspend fun library(@Header("Authorization") auth: String): LibraryResponse

    @POST("api/mobile/attempts/start")
    suspend fun startAttempt(
        @Header("Authorization") auth: String,
        @Body body: StartAttemptRequest,
    ): StartAttemptResponse

    @GET("api/mobile/attempts/{id}")
    suspend fun attempt(
        @Header("Authorization") auth: String,
        @Path("id") id: String,
    ): AttemptResponse

    @POST("api/mobile/attempts/answer")
    suspend fun answer(
        @Header("Authorization") auth: String,
        @Body body: AnswerRequest,
    ): AnswerResponse

    @POST("api/mobile/attempts/submit")
    suspend fun submit(
        @Header("Authorization") auth: String,
        @Body body: SubmitRequest,
    ): AttemptScore

    @DELETE("api/mobile/account")
    suspend fun deleteAccount(@Header("Authorization") auth: String): DeleteAccountResponse
}
