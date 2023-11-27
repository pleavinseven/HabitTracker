package com.pleavinseven.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.InsertChart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.pleavinseven.viewmodels.NavigationViewModel

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val nav: String
)

@Composable
fun BottomNavBar(navigationViewModel: NavigationViewModel, navController: NavController) {
    val navItems = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            nav = "HabitsPage"
        ),
        BottomNavigationItem(
            title = "Log",
            selectedIcon = Icons.Filled.InsertChart,
            unselectedIcon = Icons.Outlined.InsertChart,
            nav = "LogPage"
        ),
        BottomNavigationItem(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            nav = "Settings"
        ),

        )
    val selectedItemIndex by navigationViewModel.navBarPosition.collectAsState()
    NavigationBar {
        navItems.forEachIndexed { index, item ->
            NavigationBarItem(selected = selectedItemIndex == index, onClick = {
                navigationViewModel.setNavBarPosition(index)
                navController.navigate(item.nav) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }, icon = {
                Icon(
                    imageVector = if (selectedItemIndex == index) {
                        item.selectedIcon
                    } else {
                        item.unselectedIcon
                    }, contentDescription = item.title
                )
            })
        }
    }
}