package com.example.pluto.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pluto.data.model.TransactionType
import com.example.pluto.data.model.TransactionWithAccount
import com.example.pluto.ui.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Color constants for consistency
private val RedExpense = Color(0xFFE57373)
private val GreenIncome = Color(0xFF81C784)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    navController: NavController,
) {
    val viewModel: TransactionScreenViewModel = viewModel(factory = ViewModelFactory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MyMoney", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Open navigation drawer */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Handle search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_transaction") }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            MonthSelector(
                currentDate = uiState.currentDate,
                onPreviousMonth = { viewModel.onPreviousMonthClick() },
                onNextMonth = { viewModel.onNextMonthClick() }
            )
            MonthlySummary(
                expense = uiState.monthlyExpense,
                income = uiState.monthlyIncome,
                total = uiState.monthlyTotal
            )
            TransactionList(
                transactions = uiState.transactions,
                onTransactionClick = { transactionId ->
                    navController.navigate("edit_transaction/$transactionId")
                }
            )
        }
    }
}

@Composable
private fun MonthSelector(currentDate: Date, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit) {
    val monthFormat = SimpleDateFormat("MMMM, yyyy", Locale.getDefault())
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
        }
        Text(text = monthFormat.format(currentDate), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
        }
    }
}

@Composable
fun MonthlySummary(expense: Double, income: Double, total: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SummaryItem("EXPENSE", "₹%.2f".format(expense), RedExpense)
        SummaryItem("INCOME", "₹%.2f".format(income), GreenIncome)
        SummaryItem("TOTAL", "₹%.2f".format(total), MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun RowScope.SummaryItem(label: String, amount: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, letterSpacing = 1.sp)
        Text(text = amount, style = MaterialTheme.typography.bodyLarge, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun TransactionList(transactions: List<TransactionWithAccount>, onTransactionClick: (Int) -> Unit) {
    val grouped = transactions.groupBy {
        Calendar.getInstance().apply {
            time = it.transaction.date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (transactions.isEmpty()) {
            item {
                Text(
                    text = "No transactions this month.",
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            grouped.forEach { (_, dailyTransactions) ->
                val dateForHeader = dailyTransactions.first().transaction.date

                val dailyTotal = dailyTransactions.sumOf {
                    if (it.transaction.type == TransactionType.INCOME) it.transaction.amount else -it.transaction.amount
                }

                item {
                    DateHeader(date = dateForHeader, dailyTotal = dailyTotal)
                }
                items(dailyTransactions, key = { it.transaction.id }) { transactionWithAccount ->
                    TransactionRow(
                        transactionWithAccount = transactionWithAccount,
                        onClick = { onTransactionClick(transactionWithAccount.transaction.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DateHeader(date: Date, dailyTotal: Double) {
    val dateFormat = SimpleDateFormat("MMM dd, EEEE", Locale.getDefault())
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dateFormat.format(date),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "₹%.2f".format(dailyTotal),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = if (dailyTotal < 0) RedExpense else GreenIncome
        )
    }
}

private fun getIconForCategory(category: String): ImageVector {
    return when (category.lowercase()) {
        "zomato", "office food", "food" -> Icons.Default.Fastfood
        "blinkit", "shopping" -> Icons.Default.ShoppingCart
        "transportation" -> Icons.Default.DirectionsBus
        else -> Icons.Default.ReceiptLong
    }
}

@Composable
private fun TransactionRow(transactionWithAccount: TransactionWithAccount, onClick: () -> Unit) {
    val transaction = transactionWithAccount.transaction
    val account = transactionWithAccount.account

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconForCategory(transaction.category),
                    contentDescription = transaction.category,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = transaction.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(text = account.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Text(
                text = if (transaction.type == TransactionType.INCOME) "₹%.2f".format(transaction.amount) else "-₹%.2f".format(transaction.amount),
                color = if (transaction.type == TransactionType.INCOME) GreenIncome else RedExpense,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = { /* No-op */ },
            icon = { Icon(Icons.Default.Receipt, contentDescription = "Records") },
            label = { Text("Records") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* No-op */ },
            icon = { Icon(Icons.Default.Analytics, contentDescription = "Analysis") },
            label = { Text("Analysis") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* No-op */ },
            icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Accounts") },
            label = { Text("Accounts") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* No-op */ },
            icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
            label = { Text("Categories") }
        )
    }
}