package com.bennu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bennu.app.ui.screens.CourseDetailScreen
import com.bennu.app.ui.screens.DashboardScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "dashboard") {
                        composable("dashboard") {
                            DashboardScreen(
                                onNavigateToCourse = { courseId ->
                                    navController.navigate("courseDetail/$courseId")
                                }
                            )
                        }
                        composable(
                            route = "courseDetail/{courseId}",
                            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val courseId = backStackEntry.arguments?.getInt("courseId") ?: return@composable
                            CourseDetailScreen(
                                courseId = courseId,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
