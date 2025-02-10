package com.example.myappgitmanager.data.interfaces

interface ContentGetRepository<Request, Respond> {
    suspend fun contentGet(input: Request): Respond
}