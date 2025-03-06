package com.taqiyuddin.storyappdicoding.submissionakhir.data.response

import com.google.gson.annotations.SerializedName

data class CreateUserAccountResponse(

	@field:SerializedName("error")
	val isError: Boolean? = null,

	@field:SerializedName("message")
	val statusMessage: String? = null
)

data class LoginResponse(

	@field:SerializedName("loginResult")
	val loginDetails: LoginDetails? = null,

	@field:SerializedName("error")
	val isError: Boolean? = null,

	@field:SerializedName("message")
	val statusMessage: String? = null
)

data class LoginDetails(

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("name")
	val username: String? = null,

	@field:SerializedName("token")
	val authToken: String? = null
)


