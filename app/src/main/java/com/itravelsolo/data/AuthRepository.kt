package com.itravelsolo.data

import com.apollographql.apollo3.api.ApolloResponse
import com.itravelSolo.apollo.MeQuery
import com.itravelSolo.apollo.RequestEmailVerificationOtpMutation
import com.itravelSolo.apollo.SignInMutation
import com.itravelSolo.apollo.SignUpMutation
import com.itravelSolo.apollo.UpdateLocationMutation
import com.itravelSolo.apollo.VerifyEmailOtpMutation
import com.itravelSolo.apollo.type.RegisterUserInput
import com.itravelSolo.apollo.type.RequestEmailVerificationOTP
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

    suspend fun signInUser(email: String, password: String): ApolloResponse<SignInMutation.Data> {
        return apolloClient.mutation(SignInMutation(email = email, password = password)).execute()
    }

    suspend fun requestEmailVerificationOtp(email: String): ApolloResponse<RequestEmailVerificationOtpMutation.Data> {
        return apolloClient.mutation(RequestEmailVerificationOtpMutation(email = email)).execute()
    }

    suspend fun verifyEmailOTP(email: String, otp: String): ApolloResponse<VerifyEmailOtpMutation.Data> {
        return apolloClient.mutation(VerifyEmailOtpMutation(email = email, otp = otp)).execute()
    }

    suspend fun updateUserLocation(lat: Double, lon: Double, showLocation: Boolean): ApolloResponse<UpdateLocationMutation.Data> {
        return apolloClient.mutation(
            UpdateLocationMutation(
                latitude = lat,
                longitude = lon,
                showLocation = showLocation
            )
        ).execute()
    }

    suspend fun me(): ApolloResponse<MeQuery.Data> {
        return apolloClient.query(MeQuery()).execute()
    }
}