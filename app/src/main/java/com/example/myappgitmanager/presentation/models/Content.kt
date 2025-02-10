package com.example.myappgitmanager.presentation.models

data class Content(
    val name: String,
    val type: ContentType,
    val path: String,
    val size: String,
    val htmlUrl: String
)
