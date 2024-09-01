/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.feature.qr.qrCodeImport

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.mifos.mobile.core.ui.component.MifosAlertDialog
import org.mifos.mobile.feature.qr.R

@Composable
@OptIn(ExperimentalPermissionsApi::class)
internal fun PermissionBox(
    title: Int,
    description: Int,
    confirmButtonText: Int,
    dismissButtonText: Int,
    requiredPermissions: List<String>,
    modifier: Modifier = Modifier,
    onGranted: @Composable (() -> Unit)? = null,
    onDenied: @Composable (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionStates: MutableList<PermissionState> = mutableListOf()

    for (permission in requiredPermissions) {
        permissionStates.add(rememberPermissionState(permission = permission))
    }

    fun checkPermissionStatus(): Boolean {
        for (permissionState in permissionStates) {
            if (permissionState.status.shouldShowRationale) {
                return true
            }
        }
        return false
    }

    var permissionGranted by remember {
        mutableStateOf(
            requiredPermissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            },
        )
    }

    var shouldShowPermissionRationale by remember {
        mutableStateOf(
            checkPermissionStatus(),
        )
    }

    var shouldDirectUserToApplicationSettings by remember {
        mutableStateOf(false)
    }

    val decideCurrentPermissionStatus: (Boolean, Boolean) -> String =
        { granted, permissionRationale ->
            if (granted) {
                "Granted"
            } else if (permissionRationale) {
                "Rejected"
            } else {
                "Denied"
            }
        }

    var currentPermissionStatus by remember {
        mutableStateOf(
            decideCurrentPermissionStatus(
                permissionGranted,
                shouldShowPermissionRationale,
            ),
        )
    }

    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissionResults ->
            val isGranted =
                requiredPermissions.all { permissionResults[it] ?: false }

            permissionGranted = isGranted

            if (!isGranted) {
                shouldShowPermissionRationale = checkPermissionStatus()
            }
            shouldDirectUserToApplicationSettings =
                !shouldShowPermissionRationale && !permissionGranted
            currentPermissionStatus = decideCurrentPermissionStatus(
                permissionGranted,
                shouldShowPermissionRationale,
            )
        },
    )

    if (shouldShowPermissionRationale) {
        MifosAlertDialog(
            onDismissRequest = { shouldShowPermissionRationale = false },
            dismissText = stringResource(id = dismissButtonText),
            modifier = modifier,
            onConfirmation = {
                shouldShowPermissionRationale = false
                multiplePermissionLauncher.launch(requiredPermissions.toTypedArray())
            },
            confirmationText = stringResource(id = confirmButtonText),
            dialogTitle = stringResource(id = title),
            dialogText = stringResource(id = description),
        )
    }

    if (shouldDirectUserToApplicationSettings) {
        Toast.makeText(context, R.string.please_grant_us_storage_permissions, Toast.LENGTH_LONG)
            .show()
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null),
        ).also {
            context.startActivity(it)
        }
    }

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START &&
                    !permissionGranted &&
                    !shouldShowPermissionRationale
                ) {
                    multiplePermissionLauncher.launch(requiredPermissions.toTypedArray())
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        },
    )

    if (permissionGranted) {
        if (onGranted != null) {
            onGranted()
        }
    } else {
        onDenied?.invoke()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun convertToMutableBitmap(bitmap: Bitmap): Bitmap {
    return if (bitmap.config == Bitmap.Config.HARDWARE) {
        bitmap.copy(Bitmap.Config.ARGB_8888, true)
    } else {
        bitmap
    }
}
