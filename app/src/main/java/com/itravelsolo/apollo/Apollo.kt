package com.itravelsolo.apollo

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient

var authToken: String? = null

val apolloClient: ApolloClient by lazy {
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()

        Log.d("Apollo Auth", "Current token: $authToken")

        if(authToken != null) request.addHeader("Authorization", "Bearer $authToken")

        chain.proceed(request.build())
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder().addInterceptor(authInterceptor).addInterceptor(logging).build()

    // laptop ip for physical device || 10.0.2.2 for emulator
    ApolloClient.Builder().serverUrl("http://192.168.108.129:8000/graphql/").okHttpClient(okHttpClient).build()
}