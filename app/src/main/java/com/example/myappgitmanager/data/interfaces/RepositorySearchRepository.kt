package com.example.myappgitmanager.data.interfaces

interface RepositorySearchRepository<Query, Result> {
    suspend fun searchRepositories(query: Query): Result
}