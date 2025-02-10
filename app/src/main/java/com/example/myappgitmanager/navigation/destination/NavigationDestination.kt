package com.example.myappgitmanager.navigation.destination

import com.example.myappgitmanager.navigation.args.NavArgs

enum class Screen{
    SEARCH,
    USER_REPOSITORY
}

sealed class NavigationDestination(
    val baseRoute: String,
    val requiredArgs: Array<String> = emptyArray(),
    val optionalArgs: Array<String> = emptyArray(),
) {
    object Search : NavigationDestination(Screen.SEARCH.name)

    object Repository : NavigationDestination(
        Screen.USER_REPOSITORY.name,
        requiredArgs = arrayOf(NavArgs.REPOSITORY_ID, NavArgs.OWNER_ID),
        optionalArgs = arrayOf(NavArgs.PATH)
    )

    fun buildRoute(): String {
        val requiredPart = requiredArgs.joinToString("/") { "{$it}" }
        val optionalPart = optionalArgs.joinToString("&") { "$it={$it}" }

        return buildString {
            append(baseRoute)
            if (requiredPart.isNotEmpty()) append("/$requiredPart")
            if (optionalPart.isNotEmpty()) append("?$optionalPart")
        }
    }

    fun buildRouteWithArgs(args: Map<String, Any?>): String {
        val requiredPart = requiredArgs.joinToString("/") { param ->
            args[param]?.toString() ?: "{$param}"
        }

        val optionalPart = optionalArgs
            .mapNotNull { param -> args[param]?.let { "$param=$it" } }
            .joinToString("&")

        return buildString {
            append(baseRoute)
            if (requiredPart.isNotEmpty()) append("/$requiredPart")
            if (optionalPart.isNotEmpty()) append("?$optionalPart")
        }
    }
}