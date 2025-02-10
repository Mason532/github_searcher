package com.example.myappgitmanager.data.interfaces

interface ImageLoader<Input, Output> {
    suspend fun loadImage(input: Input): Output
}