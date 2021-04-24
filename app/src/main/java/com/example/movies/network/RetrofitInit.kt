package com.example.movies.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.reflect.KClass

private val gsonConverter: GsonConverterFactory = GsonConverterFactory.create()

class RetrofitInit(url: String) {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(OkHttpClient.Builder().apply {
            addInterceptor(ApiKeyInterceptor())
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
        }.build())
        .addConverterFactory(gsonConverter)
        .build()

    fun <T : Any> create(clazz: KClass<T>): T = retrofit.create(clazz.java)
}