package com.wolf.envelopebro.ui.screens.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wolf.envelopebro.R
import com.wolf.envelopebro.data.model.Transaction
import com.wolf.envelopebro.data.model.TransactionType
import com.wolf.envelopebro.ui.components.ErrorDialog
import com.wolf.envelopebro.ui.theme.PositiveAmount
import com.wolf.envelopebro.ui.theme.NegativeAmount
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.transactions)) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (uiState.transactions.isEmpty()) {
                EmptyTransactionsMessage()
            } else {
                TransactionsList(transactions = uiState.transactions)
            }

            uiState.error?.let { error ->
                ErrorDialog(
                    message = error,
                    onDismiss = viewModel::clearError
                )
            }
        }
    }
}

@Composable
fun EmptyTransactionsMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No transactions yet. Add income or transfer money to see transactions here!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun TransactionsList(transactions: List<Transaction>) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(transactions) { transaction ->
            TransactionCard(
                transaction = transaction,
                dateFormat = dateFormat,
                currencyFormat = currencyFormat
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionCard(
    transaction: Transaction,
    dateFormat: SimpleDateFormat,
    currencyFormat: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = when (transaction.type) {
                            TransactionType.INCOME -> stringResource(R.string.income)
                            TransactionType.ENVELOPE_TRANSFER -> stringResource(R.string.transfer_to_envelope)
                            TransactionType.EXPENSE -> stringResource(R.string.spend_from_envelope)
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = dateFormat.format(transaction.date),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = currencyFormat.format(transaction.amount),
                    style = MaterialTheme.typography.titleMedium,
                    color = when {
                        transaction.type == TransactionType.INCOME -> PositiveAmount
                        transaction.amount < 0 -> NegativeAmount
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            if (transaction.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
} 