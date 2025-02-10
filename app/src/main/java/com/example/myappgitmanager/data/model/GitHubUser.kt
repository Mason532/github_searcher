package com.example.myappgitmanager.data.model

import android.graphics.Bitmap
import com.example.myappgitmanager.presentation.models.User

data class GitHubUser(
    val login: String,
    val id: Int,
    val avatar_url: String,
    val html_url: String,
)

fun GitHubUser.toUser(userProfilePhoto: Bitmap? = null): User {
    return User(
        login = this.login,
        id = this.id,
        htmlUrl = this.html_url,
        picture = userProfilePhoto
    )
}