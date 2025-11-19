package com.itravelsolo.apollo

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient

val apolloClient: ApolloClient by lazy {
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()

    // laptop ip when physical device || 10.0.2.2 when emulator is being used
    ApolloClient.Builder().serverUrl("http://192.168.125.129:8000/graphql/").okHttpClient(okHttpClient).build()
}