/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.core.model.entity.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.mifos.mobile.core.common.ApiEndPoints

@Parcelize
data class DepositType(
    var id: Int? = null,
    var code: String? = null,

    var value: String? = null,
) : Parcelable {

    fun isRecurring(): Boolean {
        return ServerTypes.RECURRING.id == this.id
    }

    fun endpoint(): String {
        return ServerTypes.fromId(id!!).endpoint
    }

    fun serverType(): ServerTypes {
        return ServerTypes.fromId(id!!)
    }

    enum class ServerTypes constructor(
        val id: Int?,
        val code: String? = null,
        val endpoint: String,
    ) {
        SAVINGS(100, "depositAccountType.savingsDeposit", ApiEndPoints.SAVINGS_ACCOUNTS),
        FIXED(200, "depositAccountType.fixedDeposit", ApiEndPoints.SAVINGS_ACCOUNTS),
        RECURRING(300, "depositAccountType.recurringDeposit", ApiEndPoints.RECURRING_ACCOUNTS),
        ;

        companion object {

            fun fromId(id: Int): ServerTypes {
                for (type in values()) {
                    if (type.id == id) {
                        return type
                    }
                }
                return SAVINGS
            }
        }
    }
}
