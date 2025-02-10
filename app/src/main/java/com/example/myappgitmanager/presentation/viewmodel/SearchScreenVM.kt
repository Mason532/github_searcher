package com.example.myappgitmanager.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappgitmanager.domain.SearchAndSortItemsUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class SearchScreenState(
    var isLoading: Boolean = false,
    var data: List<Any>? = null,
    var searchScreenError: Throwable? = null
)

class SearchScreenVM(
    private val savedStateHandle: SavedStateHandle,
    private val searchUsersAndRepositories: SearchAndSortItemsUseCase<String, List<Any>>,
): ViewModel() {
    private var searchJob: Job? = null

    var state = MutableStateFlow( SearchScreenState() )
        private set

    fun resetState() {
        state.value = SearchScreenState()
    }

    fun executeQuery(query: String) {
        searchJob?.cancel()

        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            state.value = state.value.copy(isLoading = false, searchScreenError = exception)
            Log.d("SearchUsersAndRepositoriesException: ", exception.message.toString())
        }

        searchJob = viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            state.value = state.value.copy(isLoading = true, searchScreenError = null)
            val sortedList = searchUsersAndRepositories.execute(query = query)

            state.value = state.value.copy(isLoading = false, data = sortedList)
        }
    }

    fun cancelQuery() {
        searchJob?.cancel()
        searchJob = null
    }
}