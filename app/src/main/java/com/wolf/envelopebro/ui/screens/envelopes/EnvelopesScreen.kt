package com.wolf.envelopebro.ui.screens.envelopes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wolf.envelopebro.R
import com.wolf.envelopebro.data.model.Envelope
import com.wolf.envelopebro.ui.components.AmountInput
import com.wolf.envelopebro.ui.components.ErrorDialog
import com.wolf.envelopebro.ui.theme.PositiveAmount
import com.wolf.envelopebro.ui.theme.NegativeAmount
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvelopesScreen(
    viewModel: EnvelopesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.envelopes)) },
                actions = {
                    IconButton(onClick = { viewModel.showAddDialog() }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_envelope))
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (uiState.envelopes.isEmpty()) {
                EmptyEnvelopesMessage()
            } else {
                EnvelopesList(
                    envelopes = uiState.envelopes,
                    onTransferClick = { envelope ->
                        viewModel.showTransferDialog(envelope)
                    },
                    onSpendClick = { envelope ->
                        viewModel.showSpendDialog(envelope)
                    }
                )
            }

            if (uiState.showAddDialog) {
                AddEnvelopeDialog(
                    onDismiss = { viewModel.hideAddDialog() },
                    onConfirm = { name, color ->
                        viewModel.addEnvelope(name, color)
                    }
                )
            }

            if (uiState.showTransferDialog && uiState.selectedEnvelope != null) {
                TransferDialog(
                    envelope = uiState.selectedEnvelope!!,
                    onDismiss = { viewModel.hideTransferDialog() },
                    onConfirm = { amount, description ->
                        viewModel.transferToEnvelope(uiState.selectedEnvelope!!.id, amount, description)
                    }
                )
            }

            if (uiState.showSpendDialog && uiState.selectedEnvelope != null) {
                SpendDialog(
                    envelope = uiState.selectedEnvelope!!,
                    onDismiss = { viewModel.hideSpendDialog() },
                    onConfirm = { amount, description ->
                        viewModel.spendFromEnvelope(uiState.selectedEnvelope!!.id, amount, description)
                    }
                )
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
fun EmptyEnvelopesMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No envelopes yet. Tap + to create one!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun EnvelopesList(
    envelopes: List<Envelope>,
    onTransferClick: (Envelope) -> Unit,
    onSpendClick: (Envelope) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(envelopes) { envelope ->
            EnvelopeCard(
                envelope = envelope,
                onTransferClick = { onTransferClick(envelope) },
                onSpendClick = { onSpendClick(envelope) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvelopeCard(
    envelope: Envelope,
    onTransferClick: () -> Unit,
    onSpendClick: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
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
                Text(
                    text = envelope.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = currencyFormat.format(envelope.balance),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (envelope.balance >= 0) PositiveAmount else NegativeAmount
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTransferClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.transfer_to_envelope))
                }

                Button(
                    onClick = onSpendClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.errorContainer,
                        contentColor = colorScheme.onErrorContainer
                    )
                ) {
                    Text(stringResource(R.string.spend_from_envelope))
                }
            }
        }
    }
}

@Composable
fun AddEnvelopeDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, color: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val colorScheme = MaterialTheme.colorScheme
    var selectedColor by remember { mutableStateOf(colorScheme.primary.toArgb()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_envelope)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.envelope_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                // Color picker would go here
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, selectedColor) },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun TransferDialog(
    envelope: Envelope,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, description: String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.transfer_to_envelope)) },
        text = {
            Column {
                AmountInput(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text(stringResource(R.string.amount)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    amount.toDoubleOrNull()?.let { amountValue ->
                        onConfirm(amountValue, description)
                    }
                },
                enabled = amount.toDoubleOrNull() != null && amount.toDoubleOrNull()!! > 0
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun SpendDialog(
    envelope: Envelope,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, description: String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.spend_from_envelope)) },
        text = {
            Column {
                AmountInput(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text(stringResource(R.string.amount)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    amount.toDoubleOrNull()?.let { amountValue ->
                        if (amountValue <= envelope.balance) {
                            onConfirm(amountValue, description)
                        }
                    }
                },
                enabled = amount.toDoubleOrNull() != null && 
                         amount.toDoubleOrNull()!! > 0 && 
                         amount.toDoubleOrNull()!! <= envelope.balance
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
} 