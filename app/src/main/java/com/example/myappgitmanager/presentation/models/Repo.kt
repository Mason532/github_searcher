package com.example.myappgitmanager.presentation.models

import com.example.myappgitmanager.data.model.GitHubUser

data class Repo (
    val name: String,
    val description: String?,
    val owner: User,
    val createdAt: String,
    val updatedAt: String,
    val forksCount: Int,
    val watchersCount: Int,
    val stargazersCount: Int,
    val htmlUrl: String,
    var isCollapsedView: Boolean = false
)