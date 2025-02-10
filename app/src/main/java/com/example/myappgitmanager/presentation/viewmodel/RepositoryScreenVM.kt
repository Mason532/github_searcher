package com.example.myappgitmanager.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappgitmanager.data.model.GitHubContentRequest
import com.example.myappgitmanager.domain.GetRepositoryContentUseCase
import com.example.myappgitmanager.presentation.models.Content
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class RepositoryScreenState(
    var isLoading: Boolean = true,
    var data: List<Content>? = null,
    var repositoryScreenError: Throwable? = null,
)

class RepositoryScreenVM(
    private val savedStateHandle: SavedStateHandle,
    private val getContent: GetRepositoryContentUseCase<GitHubContentRequest, List<Content>>
): ViewModel() {

    var scrollPosition: Int = 0
        private set

    var scrollOffset: Int = 0
        private set

    fun updateScrollPosition(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }

    var state = MutableStateFlow( RepositoryScreenState())
        private set

    //var counter = 0

    fun getRepositoryContent(owner: String, repo: String, path: String = "") {

        val exceptionHandler = CoroutineExceptionHandler {_, exception->
            state.value = state.value.copy(isLoading = false, repositoryScreenError = exception)
            Log.d("ContentRepositoryException: ", exception.message.toString())
        }

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            state.value = state.value.copy(isLoading = true, repositoryScreenError = null)
            /*
            delay(1500)
            if (counter <= 3) {
                withContext(Dispatchers.Main) {
                    counter += 1
                }
                throw NullPointerException()
            }
             */

            val content = getContent.getContent(
                input = GitHubContentRequest(
                    owner = owner,
                    repo = repo,
                    path = path
                )
            )
            state.value = state.value.copy(isLoading = false, data = content)
        }

    }
}