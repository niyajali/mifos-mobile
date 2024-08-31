/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.feature.guarantor.navigation

import org.mifos.mobile.core.common.Constants.INDEX
import org.mifos.mobile.core.common.Constants.LOAN_ID
import org.mifos.mobile.feature.guarantor.navigation.GuarantorRoute.GUARANTOR_ADD_SCREEN_ROUTE
import org.mifos.mobile.feature.guarantor.navigation.GuarantorRoute.GUARANTOR_DETAIL_SCREEN_ROUTE
import org.mifos.mobile.feature.guarantor.navigation.GuarantorRoute.GUARANTOR_LIST_SCREEN_ROUTE
import org.mifos.mobile.feature.guarantor.navigation.GuarantorRoute.GUARANTOR_NAVIGATION_ROUTE_BASE

sealed class GuarantorNavigation(val route: String) {

    data object GuarantorScreenBase : GuarantorNavigation(
        route = "$GUARANTOR_NAVIGATION_ROUTE_BASE/{$LOAN_ID}",
    ) {
        fun passArguments(loanId: String) = "$GUARANTOR_NAVIGATION_ROUTE_BASE/$loanId"
    }

    data object GuarantorList : GuarantorNavigation(
        route = "$GUARANTOR_LIST_SCREEN_ROUTE/{$LOAN_ID}",
    ) {
        fun passArguments(loanId: String) = "$GUARANTOR_LIST_SCREEN_ROUTE/$loanId"
    }

    data object GuarantorDetails : GuarantorNavigation(
        route = "$GUARANTOR_DETAIL_SCREEN_ROUTE/{$LOAN_ID}/{$INDEX}",
    ) {
        fun passArguments(index: Int, loanId: Long) = "$GUARANTOR_DETAIL_SCREEN_ROUTE/$loanId/$index"
    }

    data object GuarantorAdd : GuarantorNavigation(
        route = "$GUARANTOR_ADD_SCREEN_ROUTE/{$LOAN_ID}/{$INDEX}",
    ) {
        fun passArguments(index: Int, loanId: Long) = "$GUARANTOR_ADD_SCREEN_ROUTE/$loanId/$index"
    }
}

object GuarantorRoute {
    const val GUARANTOR_NAVIGATION_ROUTE_BASE = "guarantor_route"
    const val GUARANTOR_LIST_SCREEN_ROUTE = "guarantor_list_screen_route"
    const val GUARANTOR_DETAIL_SCREEN_ROUTE = "guarantor_detail_screen_route"
    const val GUARANTOR_ADD_SCREEN_ROUTE = "guarantor_add_screen_route"
}
