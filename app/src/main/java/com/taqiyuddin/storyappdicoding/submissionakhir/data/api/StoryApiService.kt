package com.taqiyuddin.storyappdicoding.submissionakhir.data.api

import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.SubmitStoriesResponse
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.StoriesDetailResponse
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.StoriesResponse
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.LoginResponse
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.CreateUserAccountResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface StoryApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun createUserAccount(
        @Field("name") userName: String,
        @Field("email") userEmail: String,
        @Field("password") userPassword: String
    ) : Response<CreateUserAccountResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun authenticateUser(
        @Field("email") userEmail: String,
        @Field("password") userPassword: String
    ): Response<LoginResponse>

    @GET("stories")
    suspend fun fetchAllStories(
        @Query("page") pageNumber: Int,
        @Query("size") pageSize: Int,
        @Query("location") includeLocation: Int
    ): Response<StoriesResponse>

    @GET("stories/{id}")
    suspend fun getStoryDetails(
        @Path("id") storyId: String
    ): Response<StoriesDetailResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part("description") storyDescription: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") latitude: RequestBody? = null,
        @Part("lon") longitude: RequestBody? = null
    ): Response<SubmitStoriesResponse>

    @GET("stories")
    suspend fun fetchStoriesWithLocation(
        @Query("location") includeLocation : Int = 1,
    ): Response<StoriesResponse>
}