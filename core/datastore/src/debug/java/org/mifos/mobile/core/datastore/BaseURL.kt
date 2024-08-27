/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.core.datastore

/**
 * @author Vishwajeet
 * @since 09/06/16
 */
class BaseURL {
    val url: String? = null
        get() = field
            ?: (PROTOCOL_HTTPS + API_ENDPOINT + API_PATH)
    val defaultBaseUrl: String
        get() = PROTOCOL_HTTPS + API_ENDPOINT

    fun getUrl(endpoint: String): String {
        return endpoint + API_PATH
    }

    companion object {
        const val API_ENDPOINT = "gsoc.mifos.community"
        const val API_PATH = "/fineract-provider/api/v1/"
        const val PROTOCOL_HTTPS = "https://"
    }
}
