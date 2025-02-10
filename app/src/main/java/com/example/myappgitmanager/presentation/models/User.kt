package com.example.myappgitmanager.presentation.models

import android.graphics.Bitmap

data class User (
    val login: String,
    val id: Int,
    val htmlUrl: String,
    val picture: Bitmap? = null
)