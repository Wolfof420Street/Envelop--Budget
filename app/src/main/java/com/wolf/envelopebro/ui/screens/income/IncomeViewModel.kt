package com.wolf.envelopebro.ui.screens.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolf.envelopebro.data.repository.EnvelopeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IncomeUiState(
    val totalIncome: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false
)

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val repository: EnvelopeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomeUiState())
    val uiState: StateFlow<IncomeUiState> = _uiState.asStateFlow()

    init {
        loadTotalIncome()
    }

    private fun loadTotalIncome() {
        viewModelScope.launch {
            repository.getTotalIncome().collect { totalIncome ->
                _uiState.update { it.copy(totalIncome = totalIncome ?: 0.0) }
            }
        }
    }

    fun addIncome(amount: Double, description: String) {
        if (amount <= 0) {
            _uiState.update { it.copy(error = "Please enter a valid amount") }
            return
        }

        viewModelScope.launch {
            try {
                repository.addIncome(amount, description)
                _uiState.update { it.copy(showAddDialog = false, error = null) }
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

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
} 