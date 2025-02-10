package com.example.myappgitmanager.data.model

import com.example.myappgitmanager.presentation.models.Repo
import com.example.myappgitmanager.presentation.models.User

data class GitHubRepo(
    val name: String,
    val description: String?,
    val owner: GitHubUser,
    val created_at: String,
    val updated_at: String,
    val forks_count: Int,
    val watchers_count: Int,
    val stargazers_count: Int,
    val html_url: String
)

fun GitHubRepo.toRepo(owner: User): Repo {
    return Repo(
        name = this.name,
        description = this.description,
        owner = owner,
        createdAt = this.created_at,
        updatedAt = this.updated_at,
        forksCount = this.forks_count,
        watchersCount = this.watchers_count,
        stargazersCount = this.stargazers_count,
        htmlUrl = this.html_url,
        isCollapsedView = false
    )
}
