package com.pemmob.cicipdiary.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pemmob.cicipdiary.data.remote.RetrofitClient
import com.pemmob.cicipdiary.data.repository.MealRepository
import com.pemmob.cicipdiary.ui.detail.DetailScreen
import com.pemmob.cicipdiary.ui.detail.DetailViewModel
import com.pemmob.cicipdiary.ui.home.HomeScreen
import com.pemmob.cicipdiary.ui.home.HomeViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val repository = MealRepository(RetrofitClient.apiService)

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
            HomeScreen(
                viewModel = homeViewModel,
                navigateToDetail = { mealId ->
                    navController.navigate("detail/$mealId")
                }
            )
        }
        composable(
            route = "detail/{mealId}",
            arguments = listOf(navArgument("mealId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId").orEmpty()
            val detailViewModel: DetailViewModel = viewModel(
                factory = DetailViewModel.Factory(
                    repository = repository,
                    mealId = mealId
                )
            )
            DetailScreen(
                viewModel = detailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
