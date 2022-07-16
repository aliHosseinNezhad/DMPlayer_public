package com.gamapp.dmplayer.presenter.ui.navigation.nav_ext


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


inline fun <reified T : Route> NavHostController.navigateTo(route: T) {
    val json = Gson().toJson(route)
    this.navigate(route = "${route.getKey()}/$json")
}

inline fun <reified T : Route> NavGraphBuilder.composable(
    noinline content: @Composable (T) -> Unit
) {
    val routeId = getKey<T>()
    composable("$routeId/{data}", arguments = listOf(
        navArgument("data") {
            type = NavType.StringType
            defaultValue = null
            nullable = true
        }
    ), content = { navBackStackEntry ->
        val data = remember(navBackStackEntry){
            val dataJson = navBackStackEntry.arguments?.getString("data")
            val type = object : TypeToken<T>() {}.type
            Gson().fromJson<T>(dataJson, type)
        }
        data?.let {
            content(it)
        }
    })
}