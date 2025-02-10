package com.example.myappgitmanager.data.interfaces

import com.example.myappgitmanager.data.model.GitHubContent
import com.example.myappgitmanager.data.model.GitHubRepoResponse
import com.example.myappgitmanager.data.model.GitHubUserResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiServiceRetrofitInterface {
    @GET("search/users")
    suspend fun searchUsers(@Query("q") username: String): GitHubUserResponse

    @GET("search/repositories")
    suspend fun searchRepositories(@Query("q") repositoryname: String): GitHubRepoResponse

    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String = ""
    ): List<GitHubContent>

}

