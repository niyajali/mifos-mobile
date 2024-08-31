/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.feature.beneficiary.beneficiaryDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.mifos.mobile.core.designsystem.theme.MifosMobileTheme
import org.mifos.mobile.core.model.entity.beneficiary.Beneficiary
import org.mifos.mobile.core.ui.component.MifosTitleDescSingleLineEqual
import org.mifos.mobile.core.ui.utils.DevicePreviews
import org.mifos.mobile.feature.beneficiary.R

@Composable
internal fun BeneficiaryDetailContent(
    beneficiary: Beneficiary?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        MifosTitleDescSingleLineEqual(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .padding(4.dp),
            title = stringResource(id = R.string.beneficiary_name),
            description = beneficiary?.name.toString(),
        )

        MifosTitleDescSingleLineEqual(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .padding(4.dp),
            title = stringResource(id = R.string.account_number),
            description = beneficiary?.accountNumber.toString(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
            ) {
                MifosTitleDescSingleLineEqual(
                    modifier = Modifier.padding(vertical = 4.dp),
                    title = stringResource(id = R.string.client_name),
                    description = beneficiary?.clientName.toString(),
                )

                MifosTitleDescSingleLineEqual(
                    modifier = Modifier.padding(vertical = 4.dp),
                    title = stringResource(id = R.string.account_type),
                    description = beneficiary?.accountType.toString(),
                )

                MifosTitleDescSingleLineEqual(
                    modifier = Modifier.padding(vertical = 4.dp),
                    title = stringResource(id = R.string.transfer_limit),
                    description = beneficiary?.transferLimit.toString(),
                )

                MifosTitleDescSingleLineEqual(
                    modifier = Modifier.padding(vertical = 4.dp),
                    title = stringResource(id = R.string.office_name),
                    description = beneficiary?.officeName.toString(),
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun PreviewBeneficiaryDetailContent(
    modifier: Modifier = Modifier,
) {
    MifosMobileTheme {
        BeneficiaryDetailContent(
            beneficiary = Beneficiary(),
            modifier = modifier,
        )
    }
}
