package com.example.myappgitmanager.domain

interface SearchAndSortItemsUseCase<Input, Result> {
    suspend fun execute(query: Input): Result
}