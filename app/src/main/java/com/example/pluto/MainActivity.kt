package com.example.pluto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pluto.ui.addedit.AddEditTransactionScreen
import com.example.pluto.ui.theme.PlutoTheme
import com.example.pluto.ui.transactions.TransactionScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlutoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // NavHost is the container for all our navigation destinations
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        // Main transaction list screen
                        composable("home") {
                            // We will create this screen in the next step
                             TransactionScreen(navController = navController)
                        }

                        // Screen for adding a new transaction
                        composable("add_transaction") {
                            // We will create this screen later
                             AddEditTransactionScreen(navController = navController)
                        }

                        // Screen for editing an existing transaction
                        // The "{transactionId}" is a placeholder for the actual ID
                        composable("edit_transaction/{transactionId}") { backStackEntry ->
                            val transactionId = backStackEntry.arguments?.getString("transactionId")?.toIntOrNull()
                            // We will create this screen later
                             AddEditTransactionScreen(navController = navController, transactionId = transactionId)
                        }
                    }
                }
            }
        }
    }
}