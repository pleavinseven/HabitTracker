package com.pleavinseven.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.pleavinseven.viewmodels.NavigationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navigationViewModel: NavigationViewModel, navController: NavController) {
    Scaffold(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding(),
        topBar = {
            TopAppBar(title = {})
        },
        bottomBar = {
            BottomNavBar(navigationViewModel, navController)
        }) { innerPadding ->
        innerPadding
    }
}