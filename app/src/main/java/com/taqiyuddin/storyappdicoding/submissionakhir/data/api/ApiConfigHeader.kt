package com.taqiyuddin.storyappdicoding.submissionakhir.data.api

import com.taqiyuddin.storyappdicoding.submissionakhir.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfigHeader {
    private const val API_BASE_URL = BuildConfig.URL

    fun createApiService(token: String?): StoryApiService {
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val modifiedRequest = if (token != null && !originalRequest.url.encodedPath.contains("/login")) {
                originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                originalRequest
            }
            chain.proceed(modifiedRequest)
        }

        val loggingInterceptor = HttpLoggingInterceptor().setLevel(
            if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        )

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        return retrofit.create(StoryApiService::class.java)
    }
}
