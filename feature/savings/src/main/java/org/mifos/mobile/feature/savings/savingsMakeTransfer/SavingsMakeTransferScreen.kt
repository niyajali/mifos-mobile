/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.feature.savings.savingsMakeTransfer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import org.mifos.mobile.core.ui.utils.DevicePreviews
import org.mifos.mobile.feature.savings.R

@Composable
internal fun SavingsMakeTransferScreen(
    onCancelledClicked: () -> Unit,
    navigateBack: () -> Unit,
    reviewTransfer: (ReviewTransferPayload, TransferType) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavingsMakeTransferViewModel = hiltViewModel(),
) {
    val uiState = viewModel.savingsMakeTransferUiState.collectAsStateWithLifecycle()
    val uiData = viewModel.savingsMakeTransferUiData.collectAsStateWithLifecycle()

    SavingsMakeTransferScreen(
        navigateBack = navigateBack,
        onCancelledClicked = onCancelledClicked,
        uiState = uiState.value,
        uiData = uiData.value,
        modifier = modifier,
        reviewTransfer = { reviewTransfer(it, TransferType.SELF) },
    )
}

@Composable
private fun SavingsMakeTransferScreen(
    uiState: SavingsMakeTransferUiState,
    uiData: SavingsMakeTransferUiData,
    navigateBack: () -> Unit,
    reviewTransfer: (ReviewTransferPayload) -> Unit,
    modifier: Modifier = Modifier,
    onCancelledClicked: () -> Unit = {},
) {
    val context = LocalContext.current

    MifosScaffold(
        topBarTitleResId = if (uiData.transferType == Constants.TRANSFER_PAY_TO) {
            R.string.deposit
        } else {
            R.string.transfer
        },
        navigateBack = navigateBack,
        modifier = modifier,
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
            ) {
                SavingsMakeTransferContent(
                    uiData = uiData,
                    reviewTransfer = reviewTransfer,
                    onCancelledClicked = onCancelledClicked,
                )

                when (uiState) {
                    is SavingsMakeTransferUiState.ShowUI -> Unit

                    is SavingsMakeTransferUiState.Loading -> MifosProgressIndicatorOverlay()

                    is SavingsMakeTransferUiState.Error -> {
                        MifosErrorComponent(
                            isNetworkConnected = Network.isConnected(context),
                            isEmptyData = false,
                            isRetryEnabled = false,
                        )
                    }
                }
            }
        },
    )
}

internal class SavingsMakeTransferUiStatesPreviews :
    PreviewParameterProvider<SavingsMakeTransferUiState> {
    override val values: Sequence<SavingsMakeTransferUiState>
        get() = sequenceOf(
            SavingsMakeTransferUiState.ShowUI,
            SavingsMakeTransferUiState.Error(""),
            SavingsMakeTransferUiState.Loading,
        )
}

@DevicePreviews
@Composable
private fun SavingsMakeTransferContentPreview(
    @PreviewParameter(SavingsMakeTransferUiStatesPreviews::class)
    savingsMakeTransferUIState: SavingsMakeTransferUiState,
) {
    MifosMobileTheme {
        SavingsMakeTransferScreen(
            navigateBack = { },
            onCancelledClicked = { },
            reviewTransfer = { },
            uiState = savingsMakeTransferUIState,
            uiData = SavingsMakeTransferUiData(),
        )
    }
}
