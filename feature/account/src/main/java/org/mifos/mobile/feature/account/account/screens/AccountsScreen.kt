package org.mifos.mobile.feature.account.account.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifos.mobile.core.common.Network
import org.mifos.mobile.core.designsystem.theme.MifosMobileTheme
import org.mifos.mobile.core.model.entity.accounts.loan.LoanAccount
import org.mifos.mobile.core.model.entity.accounts.savings.SavingAccount
import org.mifos.mobile.core.model.entity.accounts.share.ShareAccount
import org.mifos.mobile.core.ui.component.EmptyDataView
import org.mifos.mobile.core.ui.component.MifosErrorComponent
import org.mifos.mobile.core.ui.component.MifosProgressIndicatorOverlay
import org.mifos.mobile.feature.account.R
import org.mifos.mobile.feature.account.utils.AccountState
import org.mifos.mobile.feature.account.viewmodel.AccountsViewModel


@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel = hiltViewModel(),
    accountType: String,
    onItemClick: (accountType: String, accountId: Long) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.accountsUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val isFiltered by viewModel.isFiltered.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterList by viewModel.filterList.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadAccounts(accountType)
    }

    AccountsScreen(
        uiState = uiState,
        isSearching = isSearching,
        isFiltered = isFiltered,
        onRetry = { viewModel.loadAccounts(accountType) },
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh(accountType) },
        searchSavingsAccountList = { accountsList ->
            viewModel.searchInSavingsList(
                accountsList,
                searchQuery
            )
        },
        searchLoanAccountList = { accountsList ->
            viewModel.searchInLoanList(
                accountsList,
                searchQuery
            )
        },
        searchShareAccountList = { accountsList ->
            viewModel.searchInSharesList(
                accountsList,
                searchQuery
            )
        },
        filterSavingsAccountList = { accountsList ->
            viewModel.getFilterSavingsAccountList(
                accountsList = accountsList,
                filterList = filterList,
                context = context
            )
        },
        filterLoanAccountList =  { accountsList ->
            viewModel.getFilterLoanAccountList(
                accountsList = accountsList,
                filterList = filterList,
                context = context
            )
        },
        filterShareAccountList =  { accountsList ->
            viewModel.getFilterShareAccountList(
                accountsList = accountsList,
                filterList = filterList,
                context = context
            )
        },
        onItemClick = { accType, accountId -> onItemClick.invoke(accType, accountId) },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AccountsScreen(
    uiState: AccountState,
    onRetry: () -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    isSearching: Boolean,
    isFiltered: Boolean,
    searchSavingsAccountList: (accountsList: List<SavingAccount>) -> List<SavingAccount>,
    searchLoanAccountList: (accountsList: List<LoanAccount>) -> List<LoanAccount>,
    searchShareAccountList: (accountsList: List<ShareAccount>) -> List<ShareAccount>,
    filterSavingsAccountList: (accountsList: List<SavingAccount>) -> List<SavingAccount>,
    filterLoanAccountList: (accountsList: List<LoanAccount>) -> List<LoanAccount>,
    filterShareAccountList: (accountsList: List<ShareAccount>) -> List<ShareAccount>,
    onItemClick: (accountType: String, accountId: Long) -> Unit,
) {
    val context = LocalContext.current
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {

        Box(modifier = Modifier.pullRefresh(pullRefreshState))
        {
            when (uiState) {
                is AccountState.Error -> {
                    MifosErrorComponent(
                        isNetworkConnected = Network.isConnected(context),
                        isRetryEnabled = true,
                        onRetry = onRetry
                    )
                }

                is AccountState.Loading -> {
                    MifosProgressIndicatorOverlay()
                }

                is AccountState.ShowSavingsAccounts -> {
                    if ((uiState.savingAccounts.isNullOrEmpty())) {
                        EmptyDataView(
                            icon = R.drawable.ic_error_black_24dp,
                            error = R.string.empty_savings_accounts,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        SavingsAccountContent(
                            accountsList = uiState.savingAccounts,
                            isSearching = isSearching,
                            isFiltered = isFiltered,
                            getUpdatedSearchList = searchSavingsAccountList,
                            onItemClick = onItemClick,
                            getUpdatedFilterList = filterSavingsAccountList
                        )
                    }
                }

                is AccountState.ShowLoanAccounts -> {
                    if ((uiState.loanAccounts.isNullOrEmpty())) {
                        EmptyDataView(
                            icon = R.drawable.ic_error_black_24dp,
                            error = R.string.empty_loan_accounts,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LoanAccountContent(
                            accountsList = uiState.loanAccounts,
                            isSearching = isSearching,
                            isFiltered = isFiltered,
                            getUpdatedSearchList = searchLoanAccountList,
                            onItemClick = onItemClick,
                            getUpdatedFilterList = filterLoanAccountList
                        )
                    }
                }

                is AccountState.ShowShareAccounts -> {
                    if ((uiState.shareAccounts.isNullOrEmpty())) {
                        EmptyDataView(
                            icon = R.drawable.ic_error_black_24dp,
                            error = R.string.empty_share_accounts,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AccountScreenShareContent(
                            accountsList = uiState.shareAccounts,
                            isSearching = isSearching,
                            isFiltered = isFiltered,
                            getUpdatedSearchList = searchShareAccountList,
                            getUpdatedFilterList = filterShareAccountList
                        )
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

class AccountsScreenPreviewProvider : PreviewParameterProvider<AccountState> {

    override val values: Sequence<AccountState>
        get() = sequenceOf(
            AccountState.Loading,
            AccountState.Error,
            AccountState.ShowLoanAccounts(listOf()),
            AccountState.ShowShareAccounts(listOf()),
            AccountState.ShowSavingsAccounts(listOf()),
        )
}

@Preview(showSystemUi = true)
@Composable
private fun AccountSavingsScreenPreview(
    @PreviewParameter(AccountsScreenPreviewProvider::class) accountUiState: AccountState
) {
    MifosMobileTheme {
        AccountsScreen(
            uiState = accountUiState,
            isSearching = true,
            isFiltered = true,
            onRetry = { },
            isRefreshing = true,
            onRefresh = { },
            filterSavingsAccountList = { _ -> listOf() },
            filterLoanAccountList = { _ -> listOf() },
            filterShareAccountList = { _ -> listOf() },
            searchLoanAccountList = { _ -> listOf() },
            searchSavingsAccountList = { _ -> listOf() },
            searchShareAccountList = { _ -> listOf() },
            onItemClick = { _, _ -> },
        )
    }
}