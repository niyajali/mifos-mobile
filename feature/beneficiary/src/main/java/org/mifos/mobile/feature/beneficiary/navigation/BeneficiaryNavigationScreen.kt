/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.feature.beneficiary.navigation

import org.mifos.mobile.core.common.Constants.BENEFICIARY_STATE
import org.mifos.mobile.core.model.enums.BeneficiaryState

const val BENEFICIARY_NAVIGATION_ROUTE = "beneficiary_route"
const val BENEFICIARY_LIST_ROUTE = "beneficiary_list_screen"
const val BENEFICIARY_DETAIL_SCREEN_ROUTE = "beneficiary_detail_screen"
const val BENEFICIARY_APPLICATION_SCREEN_ROUTE = "beneficiary_application_screen"
const val ADD_BENEFICIARY_SCREEN_ROUTE = "add_beneficiary_screen"
const val BENEFICIARY_ID = "beneficiary_position"

sealed class BeneficiaryNavigation(val route: String) {
    data object BeneficiaryBaseRoute : BeneficiaryNavigation(route = BENEFICIARY_NAVIGATION_ROUTE)

    data object BeneficiaryList : BeneficiaryNavigation(route = BENEFICIARY_LIST_ROUTE)

    data object AddBeneficiary : BeneficiaryNavigation(route = ADD_BENEFICIARY_SCREEN_ROUTE)

    data object BeneficiaryDetail :
        BeneficiaryNavigation(route = "$BENEFICIARY_DETAIL_SCREEN_ROUTE/{$BENEFICIARY_ID}") {
        fun passArguments(beneficiaryId: Int) = "$BENEFICIARY_DETAIL_SCREEN_ROUTE/$beneficiaryId"
    }

    data object BeneficiaryApplication :
        BeneficiaryNavigation(route = "$BENEFICIARY_APPLICATION_SCREEN_ROUTE/{$BENEFICIARY_ID}/{$BENEFICIARY_STATE}") {
        fun passArguments(beneficiaryId: Int, beneficiaryState: BeneficiaryState) =
            "$BENEFICIARY_APPLICATION_SCREEN_ROUTE/$beneficiaryId/$beneficiaryState"
    }
}
