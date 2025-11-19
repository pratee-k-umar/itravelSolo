package com.itravelsolo.data

import com.apollographql.apollo3.api.ApolloResponse
import com.itravelSolo.apollo.SignUpMutation
import com.itravelSolo.apollo.type.RegisterUserInput
import com.itravelsolo.apollo.apolloClient

class AuthRepository {
    suspend fun signUpUser(name: String, email: String, password: String): ApolloResponse<SignUpMutation.Data> {
        val nameParts = name.trim().split(" ", limit = 2)
        val firstName = nameParts.getOrNull(0) ?: ""
        val lastName = nameParts.getOrNull(1) ?: ""
        val input = RegisterUserInput(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName
        )
        return apolloClient.mutation(SignUpMutation(input)).execute()
    }
}