package com.wolf.envelopebro.ui.screens.envelopes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolf.envelopebro.data.model.Envelope
import com.wolf.envelopebro.data.repository.EnvelopeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EnvelopesUiState(
    val envelopes: List<Envelope> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val showTransferDialog: Boolean = false,
    val showSpendDialog: Boolean = false,
    val selectedEnvelope: Envelope? = null
)

@HiltViewModel
class EnvelopesViewModel @Inject constructor(
    private val repository: EnvelopeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnvelopesUiState())
    val uiState: StateFlow<EnvelopesUiState> = _uiState.asStateFlow()

    init {
        loadEnvelopes()
    }

    private fun loadEnvelopes() {
        viewModelScope.launch {
            repository.getAllEnvelopes().collect { envelopes ->
                _uiState.update { it.copy(envelopes = envelopes) }
            }
        }
    }

    fun addEnvelope(name: String, color: Int) {
        if (name.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a name") }
            return
        }

        viewModelScope.launch {
            try {
                val envelope = Envelope(
                    name = name,
                    balance = 0.0,
                    color = color
                )
                repository.addEnvelope(envelope)
                _uiState.update { it.copy(showAddDialog = false, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun transferToEnvelope(envelopeId: String, amount: Double, description: String) {
        if (amount <= 0) {
            _uiState.update { it.copy(error = "Please enter a valid amount") }
            return
        }

        viewModelScope.launch {
            try {
                repository.transferToEnvelope(envelopeId, amount, description)
                _uiState.update { it.copy(showTransferDialog = false, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun spendFromEnvelope(envelopeId: String, amount: Double, description: String) {
        if (amount <= 0) {
            _uiState.update { it.copy(error = "Please enter a valid amount") }
            return
        }

        viewModelScope.launch {
            try {
                repository.spendFromEnvelope(envelopeId, amount, description)
                _uiState.update { it.copy(showSpendDialog = false, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true, error = null) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun showTransferDialog(envelope: Envelope) {
        _uiState.update { it.copy(showTransferDialog = true, selectedEnvelope = envelope, error = null) }
    }

    fun hideTransferDialog() {
        _uiState.update { it.copy(showTransferDialog = false, selectedEnvelope = null) }
    }

    fun showSpendDialog(envelope: Envelope) {
        _uiState.update { it.copy(showSpendDialog = true, selectedEnvelope = envelope, error = null) }
    }

    fun hideSpendDialog() {
        _uiState.update { it.copy(showSpendDialog = false, selectedEnvelope = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
} 