package com.example.myappgitmanager.data.remote

import com.example.myappgitmanager.data.interfaces.GitHubApiServiceRetrofitInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GitHubRetrofitInstance {
    private const val BASE_URL = "https://api.github.com/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            //.client(client)
            .build()
    }

    val apiService: GitHubApiServiceRetrofitInterface by lazy {
        retrofit.create(GitHubApiServiceRetrofitInterface::class.java)
    }
}