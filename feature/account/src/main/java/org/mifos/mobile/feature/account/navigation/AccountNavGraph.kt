/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.feature.account.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import org.mifos.mobile.core.common.Constants
import org.mifos.mobile.core.model.enums.AccountType
import org.mifos.mobile.feature.account.clientAccount.screens.ClientAccountsScreen

fun NavController.navigateToClientAccountsScreen(accountType: AccountType = AccountType.SAVINGS) {
    navigate(ClientAccountsNavigation.ClientAccountsScreen.passArguments(accountType = accountType))
}

fun NavGraphBuilder.clientAccountsNavGraph(
    navController: NavController,
    navigateToAccountDetail: (AccountType, Long) -> Unit,
    navigateToLoanApplicationScreen: () -> Unit,
    navigateToSavingsApplicationScreen: () -> Unit,
) {
    navigation(
        startDestination = ClientAccountsNavigation.ClientAccountsScreen.route,
        route = ClientAccountsNavigation.ClientAccountsBase.route,
    ) {
        clientAccountsScreenRoute(
            navigateToAccountDetail = navigateToAccountDetail,
            navigateBack = navController::popBackStack,
            navigateToLoanApplicationScreen = navigateToLoanApplicationScreen,
            navigateToSavingsApplicationScreen = navigateToSavingsApplicationScreen,
        )
    }
}

fun NavGraphBuilder.clientAccountsScreenRoute(
    navigateToLoanApplicationScreen: () -> Unit,
    navigateToSavingsApplicationScreen: () -> Unit,
    navigateToAccountDetail: (AccountType, Long) -> Unit,
    navigateBack: () -> Unit,
) {
    composable(
        route = ClientAccountsNavigation.ClientAccountsScreen.route,
        arguments = listOf(
            navArgument(name = Constants.ACCOUNT_TYPE) { type = NavType.StringType },
        ),
    ) {
        ClientAccountsScreen(
            navigateBack = navigateBack,
            navigateToLoanApplicationScreen = navigateToLoanApplicationScreen,
            navigateToSavingsApplicationScreen = navigateToSavingsApplicationScreen,
            onItemClick = { accountType, accountId ->
                navigateToAccountDetail(
                    accountType,
                    accountId,
                )
            },
        )
    }
}
