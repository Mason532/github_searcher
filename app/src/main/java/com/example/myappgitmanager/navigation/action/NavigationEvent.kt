package com.example.myappgitmanager.navigation.action

import com.example.myappgitmanager.navigation.destination.NavigationDestination

sealed class NavigationEvent {
    data class NavigateTo(
        val destination: NavigationDestination,
        val args: Map<String, Any?> = emptyMap()
    ) : NavigationEvent()

    object NavigateBack : NavigationEvent()
}