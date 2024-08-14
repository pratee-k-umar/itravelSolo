package com.example.itravelsolo.auth

data class SignInResult (
    val data: UserData?,
    val errorMessage: String? = null
)

data class UserData (
    val userId: String,
    val username: String,
    val profilePictureUrl: String?
)