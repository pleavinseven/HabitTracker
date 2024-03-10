package com.pleavinseven.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.pleavinseven.ui.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTheme(
    navController: NavController,
    hasTopBar: Boolean = false,
    topBarTitle: String = "",
    topBarActions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding(),
        topBar = if (hasTopBar) {
            {
                TopAppBar(
                    title = { Text(topBarTitle) },
                    actions = topBarActions
                )
            }
        } else {
            {}
        },
        floatingActionButton = floatingActionButton,
        bottomBar = {
            BottomNavBar(navController)
        }) { contentPadding ->
        Column(Modifier.padding(contentPadding)) {
            content()
        }
    }
}
