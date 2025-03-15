package com.pardess.musicplayer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


@Composable
fun rememberMusicNavController(
    navHostController: NavHostController = rememberNavController()
): MusicNavController = remember(navHostController) {
    MusicNavController(navController = navHostController)
}

@Stable
class MusicNavController(
    val navController: NavHostController
) {
    val currentRoute: String? = navController.currentDestination?.route

    fun navigateToRoute(route: String) {
        navController.navigate(route)
    }

    fun navigateToBottomBarRoute(route: String) {
        if (route != navController.currentDestination?.route) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(findStartDestination(navController.graph).id) {
                    saveState = true
                }
            }
        }
    }

    fun saveState(key: String, value: String) {
        navController.currentBackStackEntry?.arguments?.putString(key, value)
    }

    fun navigate(route: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(route)
        }
    }

    fun upPress() {
        navController.navigateUp()
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.getLifecycle().currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}