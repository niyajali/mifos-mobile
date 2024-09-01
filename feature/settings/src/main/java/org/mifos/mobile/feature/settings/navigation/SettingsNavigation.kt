/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.feature.settings.navigation

const val SETTINGS_NAVIGATION_ROUTE_BASE = "settings_base_route"
const val SETTINGS_SCREEN_ROUTE = "settings_screen_route"

sealed class SettingsNavigation(val route: String) {
    data object SettingsBase : SettingsNavigation(route = SETTINGS_NAVIGATION_ROUTE_BASE)
    data object SettingsScreen : SettingsNavigation(route = SETTINGS_SCREEN_ROUTE)
}
