package com.example.pluto.ui.transactions

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pluto.data.model.Transaction
import com.example.pluto.data.model.TransactionType
import com.example.pluto.ui.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

private val Green = Color(0xFF1B5E20)
private val Red = Color(0xFFB71C1C)

@Composable
fun TransactionScreen(
    navController: NavController,
) {
    // The factory will handle creating the ViewModel with its repository
    val viewModel: TransactionScreenViewModel = viewModel(factory = ViewModelFactory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MonthSelectorTopBar(
                currentDate = uiState.currentDate,
                onPreviousMonth = { viewModel.onPreviousMonthClick() },
                onNextMonth = { viewModel.onNextMonthClick() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_transaction") }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            AccountTabs(
                accounts = uiState.accounts,
                selectedAccountId = uiState.selectedAccountId,
                onAccountSelected = { accountId -> viewModel.selectAccount(accountId) }
            )
            MonthlySummaryCard(
                income = uiState.monthlyIncome,
                expense = uiState.monthlyExpense,
                total = uiState.monthlyTotal
            )
            TransactionList(
                transactions = uiState.transactions,
                onDeleteTransaction = { transaction -> viewModel.onDeleteTransaction(transaction) },
                onEditTransaction = { transactionId -> navController.navigate("edit_transaction/$transactionId") }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MonthSelectorTopBar(currentDate: Date, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit) {
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    TopAppBar(
        title = {
            Text(
                text = monthFormat.format(currentDate),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onPreviousMonth) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
            }
        },
        actions = {
            IconButton(onClick = onNextMonth) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
            }
        }
    )
}

@Composable
private fun AccountTabs(
    accounts: List<com.example.pluto.data.model.Account>,
    selectedAccountId: Int?,
    onAccountSelected: (Int) -> Unit
) {
    val selectedIndex = accounts.indexOfFirst { it.accountId == selectedAccountId }.coerceAtLeast(0)
    if (accounts.isNotEmpty()) {
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            accounts.forEachIndexed { index, account ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { onAccountSelected(account.accountId) },
                    text = { Text(account.name, color = MaterialTheme.colorScheme.onPrimaryContainer) }
                )
            }
        }
    }
}

@Composable
fun MonthlySummaryCard(income: Double, expense: Double, total: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SummaryItem("Income", "₹%.2f".format(income), Green)
            SummaryItem("Expense", "₹%.2f".format(expense), Red)
            SummaryItem("Total", "₹%.2f".format(total), MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun SummaryItem(label: String, amount: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = amount, style = MaterialTheme.typography.bodyLarge, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TransactionList(
    transactions: List<Transaction>,
    onDeleteTransaction: (Transaction) -> Unit,
    onEditTransaction: (Int) -> Unit
) {
    val groupedTransactions = transactions.groupBy {
        // Group by day, resetting time fields
        Calendar.getInstance().apply {
            time = it.date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (transactions.isEmpty()) {
            item {
                Text(
                    text = "No transactions this month.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            groupedTransactions.forEach { (date, dailyTransactions) ->
                stickyHeader {
                    DateHeader(date = date, dailyTotal = dailyTransactions.sumOf { if (it.type == TransactionType.EXPENSE) it.amount else -it.amount })
                }
                items(dailyTransactions, key = { it.id }) { transaction ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) { // Swiping left
                                onDeleteTransaction(transaction)
                                true
                            } else {
                                false
                            }
                        },
                        positionalThreshold = { it * 0.25f }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            val color by animateColorAsState(
                                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) Red.copy(alpha = 0.8f) else Color.Transparent,
                                label = "background color"
                            )
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Icon")
                            }
                        }
                    ) {
                        TransactionRow(
                            transaction = transaction,
                            onClick = { onEditTransaction(transaction.id) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun DateHeader(date: Date, dailyTotal: Double) {
    val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = dateFormat.format(date), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = "₹%.2f".format(dailyTotal), style = MaterialTheme.typography.bodyMedium, color = if (dailyTotal < 0) Red else Green)
    }
}

@Composable
private fun TransactionRow(transaction: Transaction, onClick: () -> Unit) {
    val amountColor = if (transaction.type == TransactionType.INCOME) Green else Red
    val amountSign = if (transaction.type == TransactionType.INCOME) "+" else "-"

    Surface(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = transaction.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(text = transaction.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = "$amountSign₹%.2f".format(transaction.amount),
                color = amountColor,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}