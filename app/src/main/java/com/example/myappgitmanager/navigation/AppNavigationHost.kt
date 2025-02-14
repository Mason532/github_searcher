package com.example.myappgitmanager.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myappgitmanager.navigation.action.NavigationEvent
import com.example.myappgitmanager.navigation.args.NavArgs
import com.example.myappgitmanager.navigation.destination.NavigationDestination
import com.example.myappgitmanager.presentation.ui.RepositoryScreenRoot
import com.example.myappgitmanager.presentation.ui.SearchScreenRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class AppNavigationVM: ViewModel() {
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvent.asSharedFlow()

    fun navigate(action: NavigationEvent) {
        viewModelScope.launch {
            _navigationEvent.emit(action)
        }
    }
}

@Composable
fun AppNavHost(
    startDestination: String = NavigationDestination.Search.baseRoute
) {
    val navController = rememberNavController()
    val vm = koinViewModel<AppNavigationVM>()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.navigationEvents.collect { event ->
                when (event) {
                    is NavigationEvent.NavigateTo -> navController.navigate(
                        event.destination.buildRouteWithArgs(args = event.args)
                    )
                    is NavigationEvent.NavigateBack -> navController.popBackStack()
                }
            }
        }
    }


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(
            route = NavigationDestination.Search.baseRoute
        ) {
            SearchScreenRoot(onNavigationEvent = vm::navigate)
        }

        composable(
            route = NavigationDestination.Repository.buildRoute(),
            arguments = listOf(
                navArgument(NavArgs.REPOSITORY_ID) { type = NavType.StringType },
                navArgument(NavArgs.OWNER_ID) { type = NavType.StringType },
                navArgument(NavArgs.PATH) { type = NavType.StringType; nullable = true },
            )
        ) {backStackEntry->
            val repositoryId = backStackEntry.arguments?.getString(NavArgs.REPOSITORY_ID)
            val ownerId = backStackEntry.arguments?.getString(NavArgs.OWNER_ID)
            val path = backStackEntry.arguments?.getString(NavArgs.PATH)

            RepositoryScreenRoot(
                onNavigationEvent = vm::navigate,
                ownerId = ownerId!!,
                path = path,
                repositoryId = repositoryId!!
            )

        }
    }

}
