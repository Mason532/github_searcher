package com.example.myappgitmanager.data.model

data class GitHubContentRequest(
    val owner: String,
    val repo: String,
    val path: String
)