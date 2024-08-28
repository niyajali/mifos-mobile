package org.mifos.mobile.feature.savings.savings_account_transaction


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifos.mobile.core.common.Network
import org.mifos.mobile.core.designsystem.components.MifosScaffold
import org.mifos.mobile.core.designsystem.components.MifosTopBar
import org.mifos.mobile.core.designsystem.icons.MifosIcons
import org.mifos.mobile.core.designsystem.theme.MifosMobileTheme
import org.mifos.mobile.core.model.entity.accounts.savings.Transactions
import org.mifos.mobile.core.ui.component.EmptyDataView
import org.mifos.mobile.core.ui.component.MifosErrorComponent
import org.mifos.mobile.core.ui.component.MifosProgressIndicatorOverlay
import org.mifos.mobile.feature.savings.R
import java.time.Instant

@Composable
fun SavingsAccountTransactionScreen(
    viewModel: SavingAccountsTransactionViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.savingAccountsTransactionUiState.collectAsStateWithLifecycle()
    val savingsId by viewModel.savingsId.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = savingsId) {
        viewModel.loadSavingsWithAssociations(accountId = savingsId)
    }

    SavingsAccountTransactionScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        retryConnection = { viewModel.loadSavingsWithAssociations(savingsId) },
        filterList = { viewModel.filterList(filter = it) }
    )
}

@Composable
fun SavingsAccountTransactionScreen(
    uiState: SavingsAccountTransactionUiState,
    navigateBack: () -> Unit,
    retryConnection: () -> Unit,
    filterList: (SavingsTransactionFilterDataModel) -> Unit,
) {
    val context = LocalContext.current
    var transactionList by rememberSaveable { mutableStateOf(listOf<Transactions>()) }
    var isDialogOpen by rememberSaveable { mutableStateOf(false) }
    var savingsTransactionFilterDataModel by rememberSaveable {
        mutableStateOf(
            SavingsTransactionFilterDataModel(
                startDate = Instant.now().toEpochMilli(),
                endDate = Instant.now().toEpochMilli(),
                radioFilter = null,
                checkBoxFilters = mutableListOf()
            )
        )
    }

    MifosScaffold(
        topBar = {
            MifosTopBar(
                navigateBack = navigateBack,
                title = {
                    Text(
                        text = stringResource(id = R.string.savings_account_transaction),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                actions = {
                    IconButton(onClick = { isDialogOpen = true }) {
                        Icon(
                            imageVector = MifosIcons.FilterList,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues = paddingValues)) {
                when (uiState) {
                    is SavingsAccountTransactionUiState.Loading -> {
                        MifosProgressIndicatorOverlay()
                    }

                    is SavingsAccountTransactionUiState.Error -> {
                        MifosErrorComponent(
                            isNetworkConnected = Network.isConnected(context),
                            isEmptyData = false,
                            isRetryEnabled = true,
                            onRetry = retryConnection
                        )
                    }

                    is SavingsAccountTransactionUiState.Success -> {
                        if (uiState.savingAccountsTransactionList.isNullOrEmpty()) {
                            EmptyDataView(
                                icon = R.drawable.ic_compare_arrows_black_24dp,
                                error = R.string.no_transaction_found
                            )
                        } else {
                            transactionList = uiState.savingAccountsTransactionList
                            SavingsAccountTransactionContent(transactionList = transactionList)
                        }
                    }
                }
            }
        }
    )

    if (isDialogOpen) {
        SavingsTransactionFilterDialog(
            savingsTransactionFilterDataModel = savingsTransactionFilterDataModel,
            onDismiss = { isDialogOpen = false },
            filter = { filters ->
                savingsTransactionFilterDataModel = filters
                filterList(filters)
            },
        )
    }
}

class SavingsAccountTransactionUiStatesParameterProvider :
    PreviewParameterProvider<SavingsAccountTransactionUiState> {
    override val values: Sequence<SavingsAccountTransactionUiState>
        get() = sequenceOf(
            SavingsAccountTransactionUiState.Success(listOf()),
            SavingsAccountTransactionUiState.Error(""),
            SavingsAccountTransactionUiState.Loading,
        )
}

@Preview(showSystemUi = true)
@Composable
fun SavingsAccountTransactionScreenPreview(
    @PreviewParameter(SavingsAccountTransactionUiStatesParameterProvider::class) savingsAccountUiState: SavingsAccountTransactionUiState
) {
    MifosMobileTheme {
        SavingsAccountTransactionScreen(
            uiState = savingsAccountUiState,
            navigateBack = { },
            retryConnection = { },
            filterList = {   }
        )
    }
}