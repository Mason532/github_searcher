package com.example.myappgitmanager.data.interfaces

interface UserSearchRepository<Query, Result> {
    suspend fun searchUsers(query: Query): Result
}