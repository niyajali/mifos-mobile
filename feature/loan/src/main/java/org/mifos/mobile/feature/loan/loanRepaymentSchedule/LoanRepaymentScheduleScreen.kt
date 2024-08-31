/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.feature.loan.loanRepaymentSchedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifos.mobile.core.common.Network
import org.mifos.mobile.core.common.utils.DateHelper
import org.mifos.mobile.core.designsystem.components.MifosScaffold
import org.mifos.mobile.core.designsystem.theme.MifosMobileTheme
import org.mifos.mobile.core.model.entity.accounts.loan.LoanWithAssociations
import org.mifos.mobile.core.model.entity.accounts.loan.Periods
import org.mifos.mobile.core.ui.component.EmptyDataView
import org.mifos.mobile.core.ui.component.MifosErrorComponent
import org.mifos.mobile.core.ui.component.MifosProgressIndicator
import org.mifos.mobile.core.ui.utils.DevicePreviews
import org.mifos.mobile.feature.loan.R

@Composable
internal fun LoanRepaymentScheduleScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewmodel: LoanRepaymentScheduleViewModel = hiltViewModel(),
) {
    val loanRepaymentScheduleUiState by viewmodel.loanUiState.collectAsStateWithLifecycle()

    LoanRepaymentScheduleScreen(
        loanRepaymentScheduleUiState = loanRepaymentScheduleUiState,
        navigateBack = navigateBack,
        onRetry = viewmodel::loanLoanWithAssociations,
        modifier = modifier,
    )
}

@Composable
private fun LoanRepaymentScheduleScreen(
    loanRepaymentScheduleUiState: LoanUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    MifosScaffold(
        topBarTitleResId = R.string.loan_repayment_schedule,
        navigateBack = navigateBack,
        modifier = modifier,
    ) { contentPadding ->
        when (loanRepaymentScheduleUiState) {
            LoanUiState.Loading -> {
                MifosProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f)),
                )
            }

            is LoanUiState.ShowError -> {
                MifosErrorComponent(
                    isNetworkConnected = Network.isConnected(context),
                    isRetryEnabled = true,
                    onRetry = onRetry,
                )
            }

            is LoanUiState.ShowLoan -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(contentPadding),
                ) {
                    LoanRepaymentScheduleCard(loanRepaymentScheduleUiState.loanWithAssociations)
                    RepaymentScheduleTable(
                        periods = loanRepaymentScheduleUiState.loanWithAssociations.repaymentSchedule?.periods!!,
                        currency = loanRepaymentScheduleUiState.loanWithAssociations.currency?.displaySymbol
                            ?: "$",
                    )
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun LoanRepaymentScheduleCard(
    loanWithAssociations: LoanWithAssociations,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            LoanRepaymentScheduleCardItem(
                label = stringResource(id = R.string.account_number),
                value = loanWithAssociations.accountNo ?: "--",
            )
            LoanRepaymentScheduleCardItem(
                label = stringResource(id = R.string.disbursement_date),
                value = DateHelper.getDateAsString(loanWithAssociations.timeline?.expectedDisbursementDate),
            )
            LoanRepaymentScheduleCardItem(
                label = stringResource(id = R.string.no_of_payments),
                value = loanWithAssociations.numberOfRepayments.toString(),
            )
        }
    }
}

@Composable
private fun RepaymentScheduleTable(
    currency: String,
    periods: List<Periods>,
    modifier: Modifier = Modifier,
) {
    if (periods.isNotEmpty()) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(12.dp),
        ) {
            item {
                Row {
                    TableCell(text = stringResource(id = R.string.s_no), weight = 0.5f)
                    TableCell(text = stringResource(id = R.string.date), weight = 1f)
                    TableCell(text = stringResource(id = R.string.loan_balance), weight = 1f)
                    TableCell(text = stringResource(id = R.string.repayment), weight = 1f)
                }
            }
            items(periods) { period ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    TableCell(text = "${periods.indexOf(period) + 1}", weight = 0.5f)
                    TableCell(text = DateHelper.getDateAsString(period.dueDate), weight = 1f)
                    TableCell(
                        text = if (period.principalOriginalDue == null) "$currency 0.00"
                        else "$currency ${period.principalOriginalDue}",
                        weight = 1f,
                    )
                    TableCell(
                        text = "$currency ${period.principalLoanBalanceOutstanding}",
                        weight = 1f,
                    )
                }
            }
        }
    } else {
        EmptyDataView(icon = R.drawable.ic_charges, error = R.string.repayment_schedule)
    }
}

@Composable
private fun RowScope.TableCell(
    text: String,
    weight: Float,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (isSystemInDarkTheme()) Color.Gray else Color.Black

    Text(
        text = text,
        modifier = modifier
            .border(1.dp, borderColor)
            .weight(weight)
            .padding(4.dp),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun LoanRepaymentScheduleCardItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
        )
    }
}

internal class LoanRepaymentSchedulePreviewProvider : PreviewParameterProvider<LoanUiState> {
    override val values: Sequence<LoanUiState>
        get() = sequenceOf(
            LoanUiState.Loading,
            LoanUiState.ShowError(R.string.repayment_schedule),
            LoanUiState.ShowLoan(LoanWithAssociations()),
        )
}

@DevicePreviews
@Composable
private fun LoanRepaymentScheduleScreenPreview(
    @PreviewParameter(LoanRepaymentSchedulePreviewProvider::class)
    loanUiState: LoanUiState,
) {
    MifosMobileTheme {
        LoanRepaymentScheduleScreen(
            loanRepaymentScheduleUiState = loanUiState,
            navigateBack = {},
            onRetry = {},
        )
    }
}
