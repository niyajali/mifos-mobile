package org.mifos.mobile.feature.third.party.transfer.third_party_transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.mobile.core.data.repository.BeneficiaryRepository
import org.mifos.mobile.core.data.repository.ThirdPartyTransferRepository
import org.mifos.mobile.core.model.entity.beneficiary.Beneficiary
import org.mifos.mobile.core.model.entity.templates.account.AccountOption
import javax.inject.Inject

@HiltViewModel
class ThirdPartyTransferViewModel @Inject constructor(
    private val thirdPartyTransferRepositoryImp: ThirdPartyTransferRepository,
    private val beneficiaryRepositoryImp: BeneficiaryRepository
) : ViewModel() {

    private val _thirdPartyTransferUiState = MutableStateFlow<ThirdPartyTransferUiState>(
        ThirdPartyTransferUiState.Loading
    )
    val thirdPartyTransferUiState: StateFlow<ThirdPartyTransferUiState> get() = _thirdPartyTransferUiState

    private val _thirdPartyTransferUiData = MutableStateFlow<ThirdPartyTransferUiData>(
        ThirdPartyTransferUiData()
    )
    val thirdPartyTransferUiData: StateFlow<ThirdPartyTransferUiData> get() = _thirdPartyTransferUiData

    init {
        fetchTemplate()
    }

    private fun fetchTemplate() {
        viewModelScope.launch {
            combine(
                thirdPartyTransferRepositoryImp.thirdPartyTransferTemplate(),
                beneficiaryRepositoryImp.beneficiaryList()
            ) { templateResult, beneficiariesResult ->
                _thirdPartyTransferUiData.update {
                    it.copy(
                        fromAccountDetail = templateResult.fromAccountOptions,
                        toAccountOption = templateResult.toAccountOptions,
                        beneficiaries = beneficiariesResult
                    )
                }
            }.catch {
                _thirdPartyTransferUiState.value =
                    ThirdPartyTransferUiState.Error(errorMessage = it.message)
            }.collect {
                _thirdPartyTransferUiState.value = ThirdPartyTransferUiState.ShowUI
            }
        }
    }
}

sealed class ThirdPartyTransferUiState {
    data object Loading : ThirdPartyTransferUiState()
    data class Error(val errorMessage: String?) : ThirdPartyTransferUiState()
    data object ShowUI : ThirdPartyTransferUiState()
}

data class ThirdPartyTransferUiData(
    val fromAccountDetail: List<AccountOption> = listOf(),
    val toAccountOption: List<AccountOption> = listOf(),
    val beneficiaries: List<Beneficiary> = listOf(),
)

data class ThirdPartyTransferPayload(
    val payFromAccount: AccountOption,
    val beneficiaryAccount: AccountOption,
    val transferAmount: Double,
    val transferRemark: String
)
