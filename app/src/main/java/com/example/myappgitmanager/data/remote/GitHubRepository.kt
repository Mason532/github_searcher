package com.example.myappgitmanager.data.remote

import com.example.myappgitmanager.data.interfaces.ContentGetRepository
import com.example.myappgitmanager.data.interfaces.GitHubApiServiceRetrofitInterface
import com.example.myappgitmanager.data.interfaces.RepositorySearchRepository
import com.example.myappgitmanager.data.interfaces.UserSearchRepository
import com.example.myappgitmanager.data.model.GitHubContent
import com.example.myappgitmanager.data.model.GitHubContentRequest
import com.example.myappgitmanager.data.model.GitHubRepoResponse
import com.example.myappgitmanager.data.model.GitHubUserResponse

class GitHubRepository(
    private val apiService: GitHubApiServiceRetrofitInterface
): UserSearchRepository<String, GitHubUserResponse>,
    RepositorySearchRepository<String, GitHubRepoResponse>,
        ContentGetRepository<GitHubContentRequest, List<GitHubContent>>
{
    override suspend fun searchUsers(query: String): GitHubUserResponse {
        val searchQuery = "${query.trim()} in:login"
        return apiService.searchUsers(searchQuery)
    }

    override suspend fun searchRepositories(query: String): GitHubRepoResponse {
        val searchQuery = "${query.trim()} in:name"
        return apiService.searchRepositories(searchQuery)
    }

    override suspend fun contentGet(input: GitHubContentRequest): List<GitHubContent> {
        return apiService.getContent(
            owner = input.owner,
            repo = input.repo,
            path = input.path
        )
    }
}

