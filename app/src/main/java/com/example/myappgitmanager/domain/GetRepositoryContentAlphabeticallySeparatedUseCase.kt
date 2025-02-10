package com.example.myappgitmanager.domain

import android.util.Log
import com.example.myappgitmanager.data.interfaces.ContentGetRepository
import com.example.myappgitmanager.data.model.GitHubContent
import com.example.myappgitmanager.data.model.GitHubContentRequest
import com.example.myappgitmanager.data.model.toContent
import com.example.myappgitmanager.presentation.models.Content
import com.example.myappgitmanager.presentation.models.ContentType

class GetRepositoryContentAlphabeticallySeparatedUseCase(
    private val contentRepository: ContentGetRepository<GitHubContentRequest, List<GitHubContent>>
): GetRepositoryContentUseCase<GitHubContentRequest,  List<Content>> {

    override suspend fun getContent(input: GitHubContentRequest): List<Content> {
        val content = contentRepository.contentGet(input)
        return content.asSequence().map { gitHubContent ->
            gitHubContent.toContent()
        }.sortedWith(compareBy<Content> { it.type == ContentType.FILE }.thenBy { it.name }).toList()
    }

}