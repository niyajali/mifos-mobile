/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.feature.qr.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import org.mifos.mobile.core.model.entity.beneficiary.Beneficiary
import org.mifos.mobile.core.model.enums.BeneficiaryState
import org.mifos.mobile.feature.qr.qr.QrCodeReaderScreen
import org.mifos.mobile.feature.qr.qrCodeDisplay.QrCodeDisplayScreen
import org.mifos.mobile.feature.qr.qrCodeImport.QrCodeImportScreen

fun NavController.navigateToQrDisplayScreen(qrString: String) {
    navigate(QrNavigation.QrDisplayScreen.passArguments(qrString = qrString))
}

fun NavController.navigateToQrImportScreen() {
    navigate(QrNavigation.QrImportScreen.route)
}

fun NavController.navigateToQrReaderScreen() {
    navigate(QrNavigation.QrReaderScreen.route)
}

fun NavGraphBuilder.qrNavGraph(
    navController: NavHostController,
    openBeneficiaryApplication: (Beneficiary, BeneficiaryState) -> Unit,
) {
    navigation(
        startDestination = QrNavigation.QrDisplayScreen.route,
        route = QrNavigation.QrBase.route,
    ) {
        readerRoute(
            navigateBack = navController::popBackStack,
            openBeneficiaryApplication = openBeneficiaryApplication,
        )

        displayRoute(
            navigateBack = navController::popBackStack,
        )

        importRoute(
            navigateBack = navController::popBackStack,
            openBeneficiaryApplication = openBeneficiaryApplication,
        )
    }
}

fun NavGraphBuilder.readerRoute(
    navigateBack: () -> Unit,
    openBeneficiaryApplication: (Beneficiary, BeneficiaryState) -> Unit,
) {
    composable(route = QrNavigation.QrReaderScreen.route) {
        QrCodeReaderScreen(
            navigateBack = navigateBack,
            openBeneficiaryApplication = openBeneficiaryApplication,
        )
    }
}

fun NavGraphBuilder.displayRoute(
    navigateBack: () -> Unit,
) {
    composable(
        route = QrNavigation.QrDisplayScreen.route,
        arguments = listOf(
            navArgument(name = QR_ARGS) { type = NavType.StringType },
        ),
    ) {
        QrCodeDisplayScreen(
            navigateBack = navigateBack,
        )
    }
}

fun NavGraphBuilder.importRoute(
    navigateBack: () -> Unit,
    openBeneficiaryApplication: (Beneficiary, BeneficiaryState) -> Unit,
) {
    composable(route = QrNavigation.QrImportScreen.route) {
        QrCodeImportScreen(
            navigateBack = navigateBack,
            openBeneficiaryApplication = openBeneficiaryApplication,
        )
    }
}
