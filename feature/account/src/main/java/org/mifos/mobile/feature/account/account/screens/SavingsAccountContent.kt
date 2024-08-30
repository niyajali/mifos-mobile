/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.feature.account.account.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.mifos.mobile.core.common.Constants
import org.mifos.mobile.core.common.utils.CurrencyUtil
import org.mifos.mobile.core.common.utils.DateHelper
import org.mifos.mobile.core.model.entity.accounts.savings.SavingAccount
import org.mifos.mobile.feature.account.R
import org.mifos.mobile.feature.account.account.utils.AccountTypeItemIndicator

@Composable
internal fun SavingsAccountContent(
    accountsList: List<SavingAccount>,
    isSearching: Boolean,
    isFiltered: Boolean,
    getUpdatedSearchList: (accountsList: List<SavingAccount>) -> List<SavingAccount>,
    getUpdatedFilterList: (accountsList: List<SavingAccount>) -> List<SavingAccount>,
    onItemClick: (accountType: String, accountId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyColumnState = rememberLazyListState()

    var accounts by rememberSaveable { mutableStateOf(accountsList) }

    accounts = when {
        isFiltered && isSearching -> {
            getUpdatedSearchList(getUpdatedFilterList(accountsList))
        }

        isSearching -> {
            getUpdatedSearchList(accountsList)
        }

        isFiltered -> {
            getUpdatedFilterList(accountsList)
        }

        else -> {
            accountsList
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyColumnState,
    ) {
        items(items = accounts) { savingAccount ->
            AccountScreenSavingsListItem(
                savingAccount = savingAccount,
                onItemClick = onItemClick,
            )
        }
    }
}

@Composable
private fun AccountScreenSavingsListItem(
    savingAccount: SavingAccount,
    onItemClick: (accountType: String, accountId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val (color, stringResource, numColor) = when {
        savingAccount.status?.active == true -> {
            Triple(
                colorResource(R.color.deposit_green),
                DateHelper.getDateAsString(savingAccount.lastActiveTransactionDate),
                colorResource(R.color.deposit_green),
            )
        }
        savingAccount.status?.approved == true -> {
            Triple(
                colorResource(R.color.light_green),
                "${stringResource(id = R.string.feature_account_approved)} " +
                    DateHelper.getDateAsString(savingAccount.timeLine?.approvedOnDate),
                null,
            )
        }
        savingAccount.status?.submittedAndPendingApproval == true -> {
            Triple(
                colorResource(R.color.light_yellow),
                "${stringResource(id = R.string.feature_account_submitted)} " +
                    DateHelper.getDateAsString(savingAccount.timeLine?.submittedOnDate),
                null,
            )
        }
        savingAccount.status?.matured == true -> {
            Triple(
                colorResource(R.color.red_light),
                DateHelper.getDateAsString(savingAccount.lastActiveTransactionDate),
                colorResource(R.color.red_light),
            )
        }
        else -> {
            Triple(
                colorResource(R.color.light_yellow),
                "${stringResource(id = R.string.feature_account_closed)} " +
                    DateHelper.getDateAsString(savingAccount.timeLine?.closedOnDate),
                null,
            )
        }
    }

    Row(
        modifier = modifier.clickable {
            onItemClick.invoke(Constants.SAVINGS_ACCOUNTS, savingAccount.id)
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AccountTypeItemIndicator(color)

        Column(modifier = Modifier.padding(all = 12.dp)) {
            savingAccount.accountNo?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            savingAccount.productName?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelLarge,
                    color = colorResource(id = R.color.gray_dark),
                )
            }

            Text(
                text = stringResource,
                style = MaterialTheme.typography.labelLarge,
                color = colorResource(id = R.color.gray_dark),
            )
        }

        Spacer(Modifier.weight(1f))

        numColor?.let {
            val amountBalance = context.getString(
                R.string.feature_account_string_and_string,
                savingAccount.currency?.displaySymbol ?: savingAccount.currency?.code,
                CurrencyUtil.formatCurrency(context, savingAccount.accountBalance),
            )

            Text(
                text = amountBalance,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp),
                color = it,
            )
        }
    }
}
