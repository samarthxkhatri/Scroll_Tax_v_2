package com.scrolltax.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.scrolltax.app.ui.navigation.Routes
import com.scrolltax.app.ui.navigation.ScrollTaxNavHost
import com.scrolltax.app.ui.theme.ScrollTaxTheme
import com.scrolltax.app.viewmodel.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScrollTaxTheme {
                val navController = rememberNavController()
                val onboardingViewModel: OnboardingViewModel = hiltViewModel()
                val isComplete by onboardingViewModel.isOnboardingComplete.collectAsState()
                val startDestination = if (isComplete) Routes.DASHBOARD else Routes.WELCOME
                ScrollTaxNavHost(
                    navController    = navController,
                    startDestination = startDestination,
                )
            }
        }
    }
}