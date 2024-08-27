/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.core.datastore.model

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel
import org.mifos.mobile.core.datastore.SelfServiceDatabase

/**
 * Created by dilpreet on 13/9/17.
 */
@Table(database = SelfServiceDatabase::class)
class MifosNotification : BaseModel() {

    @JvmField
    @PrimaryKey
    var timeStamp: Long = 0

    @JvmField
    @Column
    var msg: String? = null

    @JvmField
    @Column
    var read: Boolean? = null
    fun getTimeStamp(): Long {
        return timeStamp
    }

    fun setTimeStamp(timeStamp: Long) {
        this.timeStamp = timeStamp
    }

    fun isRead(): Boolean {
        return read ?: false
    }

    fun setRead(read: Boolean?) {
        this.read = read
    }
}
