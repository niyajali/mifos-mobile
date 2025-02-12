/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.feature.loan.loanAccount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mifos.mobile.core.common.utils.CurrencyUtil
import org.mifos.mobile.core.common.utils.DateHelper
import org.mifos.mobile.core.designsystem.components.MifosButton
import org.mifos.mobile.core.designsystem.theme.MifosMobileTheme
import org.mifos.mobile.core.model.entity.accounts.loan.LoanWithAssociations
import org.mifos.mobile.core.ui.component.MifosTextTitleDescDoubleLine
import org.mifos.mobile.core.ui.component.MonitorListItemWithIcon
import org.mifos.mobile.core.ui.utils.DevicePreviews
import org.mifos.mobile.feature.loan.R

@Composable
internal fun LoanAccountDetailContent(
    loanWithAssociations: LoanWithAssociations,
    viewLoanSummary: () -> Unit,
    viewCharges: () -> Unit,
    viewRepaymentSchedule: () -> Unit,
    viewTransactions: () -> Unit,
    viewQr: () -> Unit,
    makePayment: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        LoanAccountDetailsCard(
            loanWithAssociations = loanWithAssociations,
            makePayment = makePayment,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoanMonitorComponent(
            viewLoanSummary = viewLoanSummary,
            viewCharges = viewCharges,
            viewRepaymentSchedule = viewRepaymentSchedule,
            viewTransactions = viewTransactions,
            viewQr = viewQr,
        )
    }
}

@Composable
private fun LoanAccountDetailsCard(
    loanWithAssociations: LoanWithAssociations,
    makePayment: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val isActive = loanWithAssociations.status?.active == true
    val currencySymbol = loanWithAssociations.summary?.currency?.displaySymbol ?: "$"

    val dueDate = if (isActive) {
        val overdueSinceDate = loanWithAssociations.summary?.getOverdueSinceDate()
        overdueSinceDate?.let { DateHelper.getDateAsString(it) }
            ?: stringResource(R.string.not_available)
    } else {
        stringResource(R.string.not_available)
    }

    val nextInstallment = getNextInstallment(loanWithAssociations, currencySymbol)

    OutlinedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            MifosTextTitleDescDoubleLine(
                title = stringResource(id = R.string.outstanding_balance),
                description = stringResource(
                    R.string.string_and_string,
                    currencySymbol,
                    CurrencyUtil.formatCurrency(
                        context = context,
                        amt = loanWithAssociations.summary?.totalOutstanding,
                    ),
                ),
                descriptionStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            )

            Spacer(modifier = Modifier.height(8.dp))

            MifosTextTitleDescDoubleLine(
                title = stringResource(id = R.string.next_installment),
                description = nextInstallment,
                descriptionStyle = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(8.dp))

            MifosTextTitleDescDoubleLine(
                title = stringResource(id = R.string.due_date),
                description = dueDate,
                descriptionStyle = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(8.dp))

            MifosTextTitleDescDoubleLine(
                title = stringResource(id = R.string.account_number),
                description = loanWithAssociations.accountNo ?: "",
                descriptionStyle = MaterialTheme.typography.bodyLarge,
            )

            Spacer(modifier = Modifier.height(8.dp))

            MifosTextTitleDescDoubleLine(
                title = stringResource(id = R.string.loan_type),
                description = loanWithAssociations.loanType?.value ?: "",
                descriptionStyle = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))

            MifosTextTitleDescDoubleLine(
                title = stringResource(id = R.string.currency),
                description = loanWithAssociations.summary?.currency?.code ?: "",
                descriptionStyle = MaterialTheme.typography.bodyLarge,
            )

            MifosButton(
                textResId = R.string.make_payment,
                modifier = Modifier.fillMaxWidth(),
                enabled = isActive,
                onClick = makePayment,
            )
        }
    }
}

@Composable
private fun LoanMonitorComponent(
    viewLoanSummary: () -> Unit,
    viewCharges: () -> Unit,
    viewRepaymentSchedule: () -> Unit,
    viewTransactions: () -> Unit,
    viewQr: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.monitor),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        MonitorListItemWithIcon(
            titleId = R.string.loan_summary,
            subTitleId = R.string.view_loan_summary,
            iconId = R.drawable.ic_surveys_48px,
            onClick = { viewLoanSummary.invoke() },
        )

        MonitorListItemWithIcon(
            titleId = R.string.loan_charges,
            subTitleId = R.string.view_charges,
            iconId = R.drawable.ic_charges,
            onClick = { viewCharges.invoke() },
        )

        MonitorListItemWithIcon(
            titleId = R.string.repayment_schedule,
            subTitleId = R.string.view_repayment,
            iconId = R.drawable.ic_charges,
            onClick = { viewRepaymentSchedule.invoke() },
        )

        MonitorListItemWithIcon(
            titleId = R.string.transactions,
            subTitleId = R.string.view_transactions,
            iconId = R.drawable.ic_compare_arrows_black_24dp,
            onClick = { viewTransactions.invoke() },
        )

        MonitorListItemWithIcon(
            titleId = R.string.qr_code,
            subTitleId = R.string.view_qr_code,
            iconId = R.drawable.ic_qrcode_scan,
            onClick = { viewQr.invoke() },
        )
    }
}

@Composable
private fun getNextInstallment(
    loanWithAssociations: LoanWithAssociations,
    currencySymbol: String,
): String {
    loanWithAssociations.repaymentSchedule?.periods?.forEach { period ->
        val dueDate = period.dueDate
        if (dueDate == loanWithAssociations.summary?.getOverdueSinceDate()) {
            return stringResource(
                id = R.string.string_and_string,
                currencySymbol,
                CurrencyUtil.formatCurrency(
                    context = LocalContext.current,
                    amt = period.totalDueForPeriod ?: 0.0,
                ),
            )
        }
    }
    return stringResource(id = R.string.not_available)
}

@DevicePreviews
@Composable
private fun LoanAccountDetailContentPreview() {
    MifosMobileTheme {
        LoanAccountDetailContent(
            loanWithAssociations = LoanWithAssociations(),
            viewLoanSummary = {},
            viewCharges = {},
            viewRepaymentSchedule = {},
            viewTransactions = {},
            viewQr = {},
            makePayment = {},
        )
    }
}
