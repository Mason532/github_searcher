package com.example.myappgitmanager.domain

import android.graphics.Bitmap
import com.example.myappgitmanager.data.interfaces.ImageLoader
import com.example.myappgitmanager.data.interfaces.RepositorySearchRepository
import com.example.myappgitmanager.data.interfaces.UserSearchRepository
import com.example.myappgitmanager.data.model.GitHubRepo
import com.example.myappgitmanager.data.model.GitHubRepoResponse
import com.example.myappgitmanager.data.model.GitHubUser
import com.example.myappgitmanager.data.model.GitHubUserResponse
import com.example.myappgitmanager.data.model.toRepo
import com.example.myappgitmanager.data.model.toUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchAndSortUsersAndReposAlphabeticallyUseCase(
    private val usersRepository: UserSearchRepository<String, GitHubUserResponse>,
    private val repositoryRepository: RepositorySearchRepository<String, GitHubRepoResponse>,
    private val imageLoader: ImageLoader<String, Bitmap>
): SearchAndSortItemsUseCase<String, List<Any>> {
    override suspend fun execute(query: String): List<Any> {
        return coroutineScope {
            val usersDeferred = async(Dispatchers.IO) { usersRepository.searchUsers(query).items }
            val reposDeferred = async(Dispatchers.IO) { repositoryRepository.searchRepositories(query).items }

            val users = usersDeferred.await().asSequence()
            val repositories = reposDeferred.await().asSequence()
                .filter { it.name.startsWith(query.trim(), ignoreCase = true) }

            val combinedList = sequenceOf(users, repositories)
                .flatten()
                .sortedBy {
                    when (it) {
                        is GitHubUser -> it.login
                        is GitHubRepo -> it.name
                        else -> ""
                    }
                }

            val mappedList = combinedList.map { item ->
                async(Dispatchers.IO) {
                    when (item) {
                        is GitHubUser -> item.toUser(
                            userProfilePhoto = imageLoader.loadImage(item.avatar_url)
                        )

                        is GitHubRepo -> item.toRepo(
                            owner = item.owner.toUser(
                                userProfilePhoto = imageLoader.loadImage(item.owner.avatar_url)
                            )
                        )

                        else -> error("Unknown type")
                    }
                }
            }.toList()

            mappedList.awaitAll()
        }
    }
}


