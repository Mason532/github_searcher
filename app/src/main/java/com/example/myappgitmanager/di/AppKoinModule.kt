package com.example.myappgitmanager.di

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import com.example.myappgitmanager.data.interfaces.ImageLoader
import com.example.myappgitmanager.data.model.GitHubContentRequest
import com.example.myappgitmanager.data.remote.BitmapImageLoader
import com.example.myappgitmanager.data.remote.GitHubRepository
import com.example.myappgitmanager.data.remote.GitHubRetrofitInstance
import com.example.myappgitmanager.domain.GetRepositoryContentAlphabeticallySeparatedUseCase
import com.example.myappgitmanager.domain.GetRepositoryContentUseCase
import com.example.myappgitmanager.domain.SearchAndSortItemsUseCase
import com.example.myappgitmanager.domain.SearchAndSortUsersAndReposAlphabeticallyUseCase
import com.example.myappgitmanager.navigation.AppNavigationVM
import com.example.myappgitmanager.presentation.models.Content
import com.example.myappgitmanager.presentation.viewmodel.RepositoryScreenVM
import com.example.myappgitmanager.presentation.viewmodel.SearchScreenVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { GitHubRetrofitInstance.apiService }

    factory { GitHubRepository(get()) }

    single<ImageLoader<String, Bitmap?>> {
        BitmapImageLoader()
    }

    single<SearchAndSortItemsUseCase<String, List<Any>>> {
        val repository: GitHubRepository  = get()
        SearchAndSortUsersAndReposAlphabeticallyUseCase(
            usersRepository = repository,
            repositoryRepository = repository,
            imageLoader = get()
        )
    }

    single<GetRepositoryContentUseCase<GitHubContentRequest,  List<Content>>> {
        val repository: GitHubRepository  = get()
        GetRepositoryContentAlphabeticallySeparatedUseCase(
            contentRepository = repository
        )
    }

    viewModel{AppNavigationVM()}

    viewModel { (savedStateHandle: SavedStateHandle) ->
        SearchScreenVM(
            savedStateHandle = savedStateHandle,
            searchUsersAndRepositories = get()
        )
    }

    viewModel {(savedStateHandle: SavedStateHandle) ->

        RepositoryScreenVM(
            savedStateHandle = savedStateHandle,
            getContent = get()
        )
    }
}

