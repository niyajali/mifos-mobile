package org.mifos.mobile.feature.guarantor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import org.mifos.mobile.core.common.Constants.INDEX
import org.mifos.mobile.core.common.Constants.LOAN_ID
import org.mifos.mobile.feature.guarantor.screens.guarantor_add.AddGuarantorScreen
import org.mifos.mobile.feature.guarantor.screens.guarantor_details.GuarantorDetailScreen
import org.mifos.mobile.feature.guarantor.screens.guarantor_list.GuarantorListScreen


fun NavController.navigateToGuarantorScreen(loanId: Long) {
    navigate(GuarantorNavigation.GuarantorScreenBase.passArguments(loanId = loanId.toString()))
}

fun NavController.navigateToGuarantorListScreen(loanId: Long) {
    navigate(GuarantorNavigation.GuarantorList.passArguments(loanId = loanId.toString()))
}

fun NavGraphBuilder.guarantorNavGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = GuarantorNavigation.GuarantorList.route,
        route = GuarantorNavigation.GuarantorScreenBase.route,
    ) {
        addGuarantorRoute(
            navigateBack = navController::popBackStack,
        )

        listGuarantorRoute(
            navigateBack = navController::popBackStack,
            addGuarantor = { loanId ->
                navController.navigate(
                    GuarantorNavigation.GuarantorAdd.passArguments(
                        index = -1,
                        loanId = loanId
                    )
                )
            },
            onGuarantorClicked = { index, loanId ->
                navController.navigate(
                    GuarantorNavigation.GuarantorDetails.passArguments(
                        index = index,
                        loanId = loanId
                    )
                )
            }
        )

        detailGuarantorRoute(
            navigateBack = navController::popBackStack,
            updateGuarantor = { index, loanId ->
                navController.navigate(
                    GuarantorNavigation.GuarantorAdd.passArguments(
                        index = index,
                        loanId = loanId
                    )
                )
            }
        )
    }
}

fun NavGraphBuilder.listGuarantorRoute(
    navigateBack: () -> Unit,
    addGuarantor: (Long) -> Unit,
    onGuarantorClicked: (Int, Long) -> Unit
) {
    composable(
        route = GuarantorNavigation.GuarantorList.route,
        arguments = listOf(
            navArgument(name = LOAN_ID) { type = NavType.StringType }
        )
    ) {
        GuarantorListScreen(
            navigateBack = navigateBack,
            addGuarantor = addGuarantor,
            onGuarantorClicked = onGuarantorClicked
        )
    }
}

fun NavGraphBuilder.detailGuarantorRoute(
    navigateBack: () -> Unit,
    updateGuarantor: (index: Int, loanId: Long) -> Unit
) {
    composable(
        route = GuarantorNavigation.GuarantorDetails.route,
        arguments = listOf(
            navArgument(name = INDEX) { type = NavType.IntType },
            navArgument(name = LOAN_ID) { type = NavType.LongType }
        )
    ) {
        GuarantorDetailScreen(
            navigateBack = navigateBack,
            updateGuarantor = updateGuarantor
        )
    }
}

fun NavGraphBuilder.addGuarantorRoute(
    navigateBack: () -> Unit
) {
    composable(
        route = GuarantorNavigation.GuarantorAdd.route,
        arguments = listOf(
            navArgument(name = INDEX) { type = NavType.IntType; defaultValue = -1 },
            navArgument(name = LOAN_ID) { type = NavType.LongType }
        )
    ) {
        AddGuarantorScreen(
            navigateBack = navigateBack,
        )
    }
}

