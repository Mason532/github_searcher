package com.example.myappgitmanager.domain

interface GetRepositoryContentUseCase<Request, Response> {
    suspend fun getContent(input: Request): Response
}