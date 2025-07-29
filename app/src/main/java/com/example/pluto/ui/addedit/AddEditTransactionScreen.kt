package com.example.pluto.ui.addedit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.pluto.data.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pluto.ui.ViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    navController: NavController,
    transactionId: Int? = null
) {

    val viewModel: AddEditTransactionViewModel = viewModel(factory = ViewModelFactory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigate back automatically after a successful save
    LaunchedEffect(uiState.hasBeenSaved) {
        if (uiState.hasBeenSaved) {
            navController.popBackStack()
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Edit Transaction" else "Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveTransaction() }) {
                        Icon(Icons.Default.Check, contentDescription = "Save Transaction")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Transaction Type (Income/Expense)
            SegmentedButtonRow(
                selectedType = uiState.transactionType,
                onTypeSelected = { viewModel.onTransactionTypeChange(it) }
            )

            // Title
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // Amount
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = { viewModel.onAmountChange(it) },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Category
            OutlinedTextField(
                value = uiState.category,
                onValueChange = { viewModel.onCategoryChange(it) },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )

            // Date Picker
            val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
            OutlinedTextField(
                value = dateFormat.format(uiState.date),
                onValueChange = { },
                label = { Text("Date") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )

            // Description
            OutlinedTextField(
                value = uiState.description ?: "",
                onValueChange = { viewModel.onDescriptionChange(it) },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.date.time)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.onDateChange(Date(it))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SegmentedButtonRow(selectedType: TransactionType, onTypeSelected: (TransactionType) -> Unit) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        SegmentedButton(
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            onClick = { onTypeSelected(TransactionType.EXPENSE) },
            selected = selectedType == TransactionType.EXPENSE
        ) {
            Text("Expense")
        }
        SegmentedButton(
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            onClick = { onTypeSelected(TransactionType.INCOME) },
            selected = selectedType == TransactionType.INCOME
        ) {
            Text("Income")
        }
    }
}