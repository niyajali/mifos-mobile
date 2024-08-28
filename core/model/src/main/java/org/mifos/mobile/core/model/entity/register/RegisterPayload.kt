/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.core.model.entity.register

/**
 * Created by dilpreet on 31/7/17.
 */

data class RegisterPayload(

    var username: String? = null,

    var firstName: String? = null,

    var lastName: String? = null,

    var email: String? = null,

    var mobileNumber: String? = null,

    var accountNumber: String? = null,

    var password: String? = null,

    var authenticationMode: String? = null,
)
