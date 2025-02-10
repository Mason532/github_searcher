package com.example.myappgitmanager.data.model

import com.example.myappgitmanager.presentation.models.Content
import com.example.myappgitmanager.presentation.models.ContentType

data class GitHubContent(
    val name: String,
    val type: String,
    val path: String,
    val size: Int,
    val html_url: String
)

fun GitHubContent.toContent(): Content {
    return Content(
        name = this.name,
        type = if (this.type == "dir") ContentType.DIR else ContentType.FILE,
        path = this.path,
        size = this.fileSizeConvertToString(this.size),
        htmlUrl = this.html_url
    )
}

fun GitHubContent.fileSizeConvertToString(sizeInBytes: Int): String {
    return when {
        sizeInBytes >= 1024 * 1024 * 1024 -> "%.2f GB".format(sizeInBytes / 1024.0 / 1024.0 / 1024.0)
        sizeInBytes >= 1024 * 1024 -> "%.2f MB".format(sizeInBytes / 1024.0 / 1024.0)
        sizeInBytes >= 1024 -> "%.2f KB".format(sizeInBytes / 1024.0)
        else -> "$sizeInBytes B"
    }
}


