package com.itravelsolo.data

import com.squareup.moshi.Json

data class SignUpRequest (
    val name: String,
    val email: String,
    val password: String
)

data class SignInRequest (
    val email: String,
    val password: String
)

data class AuthResponse (
    @param: Json(name = "user_id") val userId: String,
    val token: String,
    val email: String
)