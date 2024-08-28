package org.mifos.mobile.feature.loan.loan_review

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifos.mobile.core.common.Network
import org.mifos.mobile.core.designsystem.components.MifosTopBar
import org.mifos.mobile.core.designsystem.theme.MifosMobileTheme
import org.mifos.mobile.core.model.enums.LoanState
import org.mifos.mobile.core.ui.component.MifosProgressIndicator
import org.mifos.mobile.core.ui.component.NoInternet
import org.mifos.mobile.feature.loan.R


@Composable
fun ReviewLoanApplicationScreen(
    viewModel: ReviewLoanApplicationViewModel = hiltViewModel(),
    navigateBack: (isSuccess: Boolean) -> Unit,
) {
    val uiState by viewModel.reviewLoanApplicationUiState.collectAsStateWithLifecycle()
    val data by viewModel.reviewLoanApplicationUiData.collectAsStateWithLifecycle()

    ReviewLoanApplicationScreen(
        uiState = uiState,
        data = data,
        navigateBack = navigateBack,
        submit = { viewModel.submitLoan() }
    )
}

@Composable
fun ReviewLoanApplicationScreen(
    uiState: ReviewLoanApplicationUiState,
    data: ReviewLoanApplicationUiData,
    navigateBack: (isSuccess: Boolean) -> Unit,
    submit: () -> Unit,
) {
    val context = LocalContext.current
    Column(modifier= Modifier.fillMaxSize()) {
        MifosTopBar(
            modifier= Modifier.fillMaxWidth(),
            navigateBack = { navigateBack(false) },
            title = { Text(text = stringResource(id = R.string.update_loan)) }
        )

        Box(modifier = Modifier.weight(1f)) {
            ReviewLoanApplicationContent(modifier = Modifier.padding(16.dp), data = data, submit = submit)

            when (uiState) {
                is ReviewLoanApplicationUiState.Loading -> {
                    MifosProgressIndicator(modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(0.8f)))
                }

                is ReviewLoanApplicationUiState.Error -> {
                    ErrorComponent(errorThrowable = uiState.throwable)
                }

                is ReviewLoanApplicationUiState.Success -> {
                    when (uiState.loanState) {
                        LoanState.CREATE ->
                            LaunchedEffect(key1 = true) {
                                Toast.makeText(context, context.getString(R.string.loan_application_submitted_successfully), Toast.LENGTH_SHORT).show()
                            }

                        LoanState.UPDATE ->
                            LaunchedEffect(key1 = true) {
                                Toast.makeText(context, context.getString(R.string.loan_application_updated_successfully), Toast.LENGTH_SHORT).show()
                            }
                    }
                    navigateBack(true)
                }

                is ReviewLoanApplicationUiState.ReviewLoanUiReady -> Unit
            }
        }
    }
}


@Composable
fun ErrorComponent(
    errorThrowable: Throwable,
) {
    val context = LocalContext.current
    if (!Network.isConnected(context)) {
        NoInternet(
            error = R.string.no_internet_connection,
            isRetryEnabled = false,
        )
    } else {
        LaunchedEffect(errorThrowable) {
            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
        }
    }
}


class UiStatesParameterProvider : PreviewParameterProvider<ReviewLoanApplicationUiState> {
    override val values: Sequence<ReviewLoanApplicationUiState>
        get() = sequenceOf(
            ReviewLoanApplicationUiState.ReviewLoanUiReady,
            ReviewLoanApplicationUiState.Error(throwable = Throwable()),
            ReviewLoanApplicationUiState.Loading,
            ReviewLoanApplicationUiState.Success(loanState = LoanState.CREATE)
        )
}


@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ReviewLoanApplicationScreenPreview(
    @PreviewParameter(UiStatesParameterProvider::class) reviewLoanApplicationUiState: ReviewLoanApplicationUiState
) {
    MifosMobileTheme {
        ReviewLoanApplicationScreen(
            uiState = reviewLoanApplicationUiState,
            data = ReviewLoanApplicationUiData(),
            {}, {}
        )
    }
}


