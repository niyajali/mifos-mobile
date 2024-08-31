/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.feature.loan.loanAccountApplication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.mifos.mobile.core.common.Constants
import org.mifos.mobile.core.common.utils.DateHelper
import org.mifos.mobile.core.common.utils.getTodayFormatted
import org.mifos.mobile.core.data.repository.LoanRepository
import org.mifos.mobile.core.model.entity.accounts.loan.LoanWithAssociations
import org.mifos.mobile.core.model.entity.templates.loans.LoanTemplate
import org.mifos.mobile.core.model.enums.LoanState
import org.mifos.mobile.core.network.Result
import org.mifos.mobile.core.network.asResult
import org.mifos.mobile.feature.loan.R
import org.mifos.mobile.feature.loan.loanAccountApplication.LoanApplicationUiState.Loading
import java.time.Instant
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
internal class LoanApplicationViewModel @Inject constructor(
    private val loanRepositoryImp: LoanRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var loanUiState: StateFlow<LoanApplicationUiState> = MutableStateFlow(Loading)

    val loanId = savedStateHandle.getStateFlow<Long?>(key = Constants.LOAN_ID, initialValue = null)
    val loanState = savedStateHandle.getStateFlow(
        key = Constants.LOAN_STATE,
        initialValue = LoanState.CREATE,
    )

    var loanWithAssociations: StateFlow<LoanWithAssociations?> = loanId
        .flatMapLatest {
            loanRepositoryImp.getLoanWithAssociations(Constants.TRANSACTIONS, it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    private val _loanApplicationScreenData = MutableStateFlow(LoanApplicationScreenData())
    val loanApplicationScreenData: StateFlow<LoanApplicationScreenData> = _loanApplicationScreenData

    var loanTemplate: LoanTemplate = LoanTemplate()
    var productId: Int = 0
    var purposeId: Int = 0
    private var isLoanUpdatePurposesInitialization: Boolean = true

    init {
        _loanApplicationScreenData.update {
            it.copy(
                submittedDate = getTodayFormatted(),
                disbursementDate = getTodayFormatted(),
            )
        }
    }

    fun loadLoanApplicationTemplate(loanState: LoanState) {
        loanUiState = loanRepositoryImp.template()
            .asResult()
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        loanTemplate = result.data ?: LoanTemplate()
                        if (loanState == LoanState.CREATE) {
                            showLoanTemplate(loanTemplate = loanTemplate)
                        } else {
                            showUpdateLoanTemplate(loanTemplate = loanTemplate)
                        }
                        LoanApplicationUiState.Success
                    }

                    is Result.Loading -> Loading
                    is Result.Error -> LoanApplicationUiState.Error(R.string.error_fetching_template)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Loading,
            )
    }

    private fun loadLoanApplicationTemplateByProduct(productId: Int?, loanState: LoanState) {
        loanUiState = loanRepositoryImp.getLoanTemplateByProduct(productId)
            .asResult()
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        result.data?.let {
                            if (loanState == LoanState.CREATE) {
                                showLoanTemplateByProduct(
                                    loanTemplate = it,
                                )
                            } else {
                                showUpdateLoanTemplateByProduct(loanTemplate = it)
                            }
                        }
                        LoanApplicationUiState.Success
                    }

                    is Result.Loading -> Loading
                    is Result.Error -> LoanApplicationUiState.Error(R.string.error_fetching_template)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Loading,
            )
    }

    private fun showLoanTemplate(loanTemplate: LoanTemplate) {
        val listLoanProducts = refreshLoanProductList(loanTemplate = loanTemplate)
        _loanApplicationScreenData.update {
            it.copy(listLoanProducts = listLoanProducts)
        }
    }

    private fun showUpdateLoanTemplate(loanTemplate: LoanTemplate) {
        val listLoanProducts = refreshLoanProductList(loanTemplate = loanTemplate)
        _loanApplicationScreenData.update {
            it.copy(
                listLoanProducts = listLoanProducts,
                selectedLoanProduct = loanWithAssociations.value?.loanProductName,
                accountNumber = loanWithAssociations.value?.accountNo,
                clientName = loanWithAssociations.value?.clientName,
                currencyLabel = loanWithAssociations.value?.currency?.displayLabel,
                principalAmount = String.format(
                    Locale.getDefault(),
                    "%.2f",
                    loanWithAssociations.value?.principal,
                ),
                submittedDate = DateHelper.getDateAsString(
                    loanWithAssociations.value?.timeline?.submittedOnDate,
                    "dd-MM-yyyy",
                ),
                disbursementDate = DateHelper.getDateAsString(
                    loanWithAssociations.value?.timeline?.expectedDisbursementDate,
                    "dd-MM-yyyy",
                ),
            )
        }
    }

    private fun showLoanTemplateByProduct(loanTemplate: LoanTemplate) {
        val loanPurposeList = refreshLoanPurposeList(loanTemplate = loanTemplate)
        _loanApplicationScreenData.update {
            it.copy(
                listLoanPurpose = loanPurposeList,
                selectedLoanPurpose = loanPurposeList[0],
                accountNumber = loanTemplate.clientAccountNo,
                clientName = loanTemplate.clientName,
                currencyLabel = loanTemplate.currency?.displayLabel,
                principalAmount = String.format(
                    Locale.getDefault(),
                    "%.2f",
                    loanTemplate.principal,
                ),
            )
        }
    }

    private fun showUpdateLoanTemplateByProduct(loanTemplate: LoanTemplate) {
        val loanPurposeList = refreshLoanPurposeList(loanTemplate = loanTemplate)
        if (isLoanUpdatePurposesInitialization && loanWithAssociations.value?.loanPurposeName != null) {
            _loanApplicationScreenData.update {
                it.copy(
                    listLoanPurpose = loanPurposeList,
                    selectedLoanPurpose = loanPurposeList[0],
                )
            }
        } else {
            _loanApplicationScreenData.update {
                it.copy(
                    listLoanPurpose = loanPurposeList,
                    selectedLoanPurpose = loanWithAssociations.value?.loanPurposeName,
                    accountNumber = loanTemplate.clientAccountNo,
                    clientName = loanTemplate.clientName,
                    currencyLabel = loanTemplate.currency?.displayLabel,
                    principalAmount = String.format(
                        Locale.getDefault(),
                        "%.2f",
                        loanTemplate.principal,
                    ),
                )
            }
        }
    }

    private fun refreshLoanPurposeList(loanTemplate: LoanTemplate): MutableList<String?> {
        val loanPurposeList = mutableListOf<String?>()
        loanPurposeList.add("Purpose not provided")
        for (loanPurposeOptions in loanTemplate.loanPurposeOptions) {
            loanPurposeList.add(loanPurposeOptions.name)
        }
        return loanPurposeList
    }

    private fun refreshLoanProductList(loanTemplate: LoanTemplate): List<String?> {
        val loanProductList = _loanApplicationScreenData.value.listLoanProducts.toMutableList()
        for ((_, name) in loanTemplate.productOptions) {
            if (!loanProductList.contains(name)) {
                loanProductList.add(name)
            }
        }
        return loanProductList
    }

    fun productSelected(position: Int) {
        productId = loanTemplate.productOptions[position].id ?: 0
        loadLoanApplicationTemplateByProduct(productId, loanState.value)
        _loanApplicationScreenData.update {
            it.copy(selectedLoanProduct = loanApplicationScreenData.value.listLoanProducts[position])
        }
    }

    fun purposeSelected(position: Int) {
        loanTemplate.loanPurposeOptions.let {
            if (it.size > position) {
                purposeId = loanTemplate.loanPurposeOptions[position].id ?: 0
            }
        }
        _loanApplicationScreenData.update {
            it.copy(selectedLoanPurpose = loanApplicationScreenData.value.listLoanPurpose[position])
        }
    }

    fun setDisburseDate(date: String) {
        _loanApplicationScreenData.update { it.copy(disbursementDate = date) }
    }

    fun setPrincipalAmount(amount: String) {
        _loanApplicationScreenData.update { it.copy(principalAmount = amount) }
    }
}

internal data class LoanApplicationScreenData(
    var accountNumber: String? = null,
    var clientName: String? = null,
    var listLoanProducts: List<String?> = listOf(),
    var selectedLoanProduct: String? = null,
    var listLoanPurpose: List<String?> = listOf(),
    var selectedLoanPurpose: String? = null,
    var principalAmount: String? = null,
    var currencyLabel: String? = null,
    var selectedDisbursementDate: Instant? = null,
    var disbursementDate: String? = null,
    var submittedDate: String? = null,
)

internal sealed class LoanApplicationUiState {
    data object Loading : LoanApplicationUiState()
    data object Success : LoanApplicationUiState()
    data class Error(val errorMessageId: Int) : LoanApplicationUiState()
}
