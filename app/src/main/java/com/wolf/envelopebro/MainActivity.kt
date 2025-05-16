package com.wolf.envelopebro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wolf.envelopebro.ui.screens.envelopes.EnvelopesScreen
import com.wolf.envelopebro.ui.screens.income.IncomeScreen
import com.wolf.envelopebro.ui.screens.transactions.TransactionsScreen
import com.wolf.envelopebro.ui.theme.EnvelopeBroTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.text.Typography.dagger

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnvelopeBroTheme {
                EnvelopeBroAppContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvelopeBroAppContent() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Envelopes,
        Screen.Income,
        Screen.Transactions
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(painterResource(screen.iconResId), contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Envelopes.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Envelopes.route) { EnvelopesScreen() }
            composable(Screen.Income.route) { IncomeScreen() }
            composable(Screen.Transactions.route) { TransactionsScreen() }
        }
    }
}

sealed class Screen(val route: String, val resourceId: Int, val iconResId: Int) {
    object Envelopes : Screen("envelopes", R.string.envelopes, R.drawable.ic_envelope)
    object Income : Screen("income", R.string.income, R.drawable.ic_income)
    object Transactions : Screen("transactions", R.string.transactions, R.drawable.ic_transactions)
}