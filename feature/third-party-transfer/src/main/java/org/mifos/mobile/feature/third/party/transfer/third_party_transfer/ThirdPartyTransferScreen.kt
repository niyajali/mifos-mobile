package org.mifos.mobile.feature.third.party.transfer.third_party_transfer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifos.mobile.core.common.Network
import org.mifos.mobile.core.designsystem.components.MifosScaffold
import org.mifos.mobile.core.designsystem.theme.MifosMobileTheme
import org.mifos.mobile.core.model.entity.payload.ReviewTransferPayload
import org.mifos.mobile.core.model.enums.TransferType
import org.mifos.mobile.core.ui.component.MifosErrorComponent
import org.mifos.mobile.core.ui.component.MifosProgressIndicatorOverlay
import org.mifos.mobile.feature.third.party.transfer.R

@Composable
fun ThirdPartyTransferScreen(
    viewModel: ThirdPartyTransferViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    addBeneficiary: () -> Unit,
    reviewTransfer: (ReviewTransferPayload, TransferType) -> Unit
) {
    val uiState by viewModel.thirdPartyTransferUiState.collectAsStateWithLifecycle()
    val uiData by viewModel.thirdPartyTransferUiData.collectAsStateWithLifecycle()

    ThirdPartyTransferScreen(
        uiState = uiState,
        uiData = uiData,
        navigateBack = navigateBack,
        addBeneficiary = addBeneficiary,
        reviewTransfer = { reviewTransfer(it, TransferType.TPT) }
    )
}

@Composable
fun ThirdPartyTransferScreen(
    uiState: ThirdPartyTransferUiState,
    uiData: ThirdPartyTransferUiData,
    navigateBack: () -> Unit,
    addBeneficiary: () -> Unit,
    reviewTransfer: (ReviewTransferPayload) -> Unit
) {
    val context = LocalContext.current
    MifosScaffold(
        topBarTitleResId = R.string.third_party_transfer,
        navigateBack = navigateBack,
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues = paddingValues)) {
               when(uiState) {
                   is ThirdPartyTransferUiState.ShowUI -> {
                       ThirdPartyTransferContent(
                           accountOption = uiData.fromAccountDetail,
                           toAccountOption = uiData.toAccountOption,
                           beneficiaryList = uiData.beneficiaries,
                           navigateBack = navigateBack,
                           addBeneficiary = addBeneficiary,
                           reviewTransfer = reviewTransfer
                       )
                   }

                   is ThirdPartyTransferUiState.Loading -> {
                       MifosProgressIndicatorOverlay()
                   }

                   is ThirdPartyTransferUiState.Error -> {
                       MifosErrorComponent(
                           message = uiState.errorMessage ?: stringResource(id = R.string.error_fetching_third_party_transfer_template),
                           isNetworkConnected = Network.isConnected(context),
                       )
                   }
               }
            }
        }
    )
}

class SavingsMakeTransferUiStatesPreviews : PreviewParameterProvider<ThirdPartyTransferUiState> {
    override val values: Sequence<ThirdPartyTransferUiState>
        get() = sequenceOf(
            ThirdPartyTransferUiState.ShowUI,
            ThirdPartyTransferUiState.Error(""),
            ThirdPartyTransferUiState.Loading,
        )
}

@Preview(showSystemUi = true)
@Composable
fun ThirdPartyTransferScreenPreview(
    @PreviewParameter(SavingsMakeTransferUiStatesPreviews::class) thirdPartyTransferUiState: ThirdPartyTransferUiState
) {
    MifosMobileTheme {
        ThirdPartyTransferScreen(
            uiState = thirdPartyTransferUiState,
            uiData = ThirdPartyTransferUiData(),
            navigateBack = {},
            addBeneficiary = {},
            reviewTransfer = {}
        )
    }
}