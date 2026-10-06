package com.scrolltax.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.scrolltax.app.ui.screens.dashboard.DashboardScreen
import com.scrolltax.app.ui.screens.onboarding.OnboardingScreen
import com.scrolltax.app.ui.screens.privacy.PrivacyScreen
import com.scrolltax.app.ui.screens.reports.ReportsScreen
import com.scrolltax.app.ui.screens.settings.SettingsScreen
import com.scrolltax.app.ui.screens.welcome.WelcomeScreen

object Routes {
    const val WELCOME    = "welcome"
    const val ONBOARDING = "onboarding"
    const val DASHBOARD  = "dashboard"
    const val REPORTS    = "reports"
    const val SETTINGS   = "settings"
    const val PRIVACY    = "privacy"
}

@Composable
fun ScrollTaxNavHost(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination,
        enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 } },
        exitTransition  = { fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 4 } },
        popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 4 } },
        popExitTransition  = { fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { it / 4 } },
    ) {
        composable(Routes.WELCOME)    { WelcomeScreen(onContinue = { navController.navigate(Routes.ONBOARDING) { popUpTo(Routes.WELCOME) { inclusive = true } } }) }
        composable(Routes.ONBOARDING) { OnboardingScreen(onFinish = { navController.navigate(Routes.DASHBOARD) { popUpTo(Routes.ONBOARDING) { inclusive = true } } }) }
        composable(Routes.DASHBOARD)  { DashboardScreen(onNavigateToReports = { navController.navigate(Routes.REPORTS) }, onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }, onNavigateToPrivacy = { navController.navigate(Routes.PRIVACY) }) }
        composable(Routes.REPORTS)    { ReportsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SETTINGS)   { SettingsScreen(onBack = { navController.popBackStack() }, onNavigateToPrivacy = { navController.navigate(Routes.PRIVACY) }) }
        composable(Routes.PRIVACY)    { PrivacyScreen(onBack = { navController.popBackStack() }) }
    }
}