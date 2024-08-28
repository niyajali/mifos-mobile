package org.mifos.mobile.feature.savings.savings_make_transfer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifos.mobile.core.common.Constants
import org.mifos.mobile.core.common.Network
import org.mifos.mobile.core.designsystem.components.MifosScaffold
import org.mifos.mobile.core.designsystem.theme.MifosMobileTheme
import org.mifos.mobile.core.model.entity.payload.ReviewTransferPayload
import org.mifos.mobile.core.model.enums.TransferType
import org.mifos.mobile.core.ui.component.MifosErrorComponent
import org.mifos.mobile.core.ui.component.MifosProgressIndicatorOverlay
import org.mifos.mobile.feature.savings.R

@Composable
fun SavingsMakeTransferScreen(
    viewModel: SavingsMakeTransferViewModel = hiltViewModel(),
    onCancelledClicked: () -> Unit,
    navigateBack: () -> Unit,
    reviewTransfer: (ReviewTransferPayload, TransferType) -> Unit
) {
    val uiState = viewModel.savingsMakeTransferUiState.collectAsStateWithLifecycle()
    val uiData = viewModel.savingsMakeTransferUiData.collectAsStateWithLifecycle()

    SavingsMakeTransferScreen(
        navigateBack = navigateBack,
        onCancelledClicked = onCancelledClicked,
        uiState = uiState.value,
        uiData = uiData.value,
        reviewTransfer = { reviewTransfer(it, TransferType.SELF) }
    )
}

@Composable
fun SavingsMakeTransferScreen(
    uiState: SavingsMakeTransferUiState,
    uiData: SavingsMakeTransferUiData,
    onCancelledClicked: () -> Unit = {},
    navigateBack: () -> Unit,
    reviewTransfer: (ReviewTransferPayload) -> Unit
) {
    val context = LocalContext.current

    MifosScaffold(
        topBarTitleResId = if(uiData.transferType == Constants.TRANSFER_PAY_TO) R.string.deposit
        else R.string.transfer,
        navigateBack = navigateBack,
        content = {
            Box(modifier = Modifier.padding(it).fillMaxSize()) {
                SavingsMakeTransferContent(
                    uiData = uiData,
                    onCancelledClicked = onCancelledClicked,
                    reviewTransfer = reviewTransfer
                )

                when(uiState) {
                    is SavingsMakeTransferUiState.ShowUI -> Unit

                    is SavingsMakeTransferUiState.Loading -> { MifosProgressIndicatorOverlay() }

                    is SavingsMakeTransferUiState.Error -> {
                        MifosErrorComponent(
                            isNetworkConnected = Network.isConnected(context),
                            isEmptyData = false,
                            isRetryEnabled = false,
                        )
                    }
                }
            }
        }
    )
}



class SavingsMakeTransferUiStatesPreviews : PreviewParameterProvider<SavingsMakeTransferUiState> {
    override val values: Sequence<SavingsMakeTransferUiState>
        get() = sequenceOf(
            SavingsMakeTransferUiState.ShowUI,
            SavingsMakeTransferUiState.Error(""),
            SavingsMakeTransferUiState.Loading,
        )
}

@Preview(showSystemUi = true)
@Composable
fun SavingsMakeTransferContentPreview(
    @PreviewParameter(SavingsMakeTransferUiStatesPreviews::class) savingsMakeTransferUIState: SavingsMakeTransferUiState
) {
    MifosMobileTheme {
        SavingsMakeTransferScreen(
            navigateBack = {  },
            onCancelledClicked = {  },
            reviewTransfer = {  },
            uiState = savingsMakeTransferUIState,
            uiData = SavingsMakeTransferUiData()
        )
    }
}