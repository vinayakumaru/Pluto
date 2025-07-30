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
                            TransactionScreen(navController = navController)
                        }

                        // Screen for adding a new transaction
                        composable("add_transaction") {
                            AddEditTransactionScreen(navController = navController)
                        }

                        // Screen for editing an existing transaction
                        composable("edit_transaction/{transactionId}") {
                            AddEditTransactionScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}