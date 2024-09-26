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

import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @author Vishwajeet
 * @since 21/06/16
 */
class SelfServiceInterceptor(private val preferencesHelper: PreferencesHelper) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val chainRequest = chain.request()
        val builder = chainRequest.newBuilder()
            .header(HEADER_TENANT, preferencesHelper.tenant!!)
            .header(CONTENT_TYPE, "application/json")
        if (!TextUtils.isEmpty(preferencesHelper.token)) {
            builder.header(HEADER_AUTH, preferencesHelper.token!!)
        }
        val request = builder.build()
        return chain.proceed(request)
    }

    companion object {
        const val HEADER_TENANT = "Fineract-Platform-TenantId"
        const val HEADER_AUTH = "Authorization"
        const val DEFAULT_TENANT = "gsoc"
        const val CONTENT_TYPE = "Content-Type"
    }
}
