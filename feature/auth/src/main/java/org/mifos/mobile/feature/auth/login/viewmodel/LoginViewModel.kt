/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.feature.auth.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.mifos.mobile.core.data.repository.ClientRepository
import org.mifos.mobile.core.data.repository.UserAuthRepository
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val userAuthRepositoryImp: UserAuthRepository,
    private val clientRepositoryImp: ClientRepository,
) : ViewModel() {

    private var _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val loginUiState: StateFlow<LoginUiState> get() = _loginUiState

    /**
     * This method attempts to authenticate the user from
     * the server and then persist the authentication data if we successfully
     * authenticate the credentials or notify about any errors.
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginUiState.value = LoginUiState.Loading
            userAuthRepositoryImp.login(username, password).catch {
                _loginUiState.value = LoginUiState.Error
            }.collect {
                clientRepositoryImp.saveAuthenticationTokenForSession(it)
                _loginUiState.value = LoginUiState.LoginSuccess
            }
        }
    }

    /**
     * This method fetches the Client, associated with current Access Token.
     */
    fun loadClient() {
        viewModelScope.launch {
            clientRepositoryImp.loadClient().catch {
                _loginUiState.value = LoginUiState.Error
                clientRepositoryImp.clearPrefHelper()
                clientRepositoryImp.reInitializeService()
            }.collect {
                if (it.pageItems.isNotEmpty()) {
                    val clientId = it.pageItems[0].id.toLong()
                    val clientName = it.pageItems[0].displayName
                    clientRepositoryImp.setClientId(clientId)
                    clientRepositoryImp.reInitializeService()
                    _loginUiState.value = LoginUiState.LoadClientSuccess(clientName)
                }
            }
        }
    }
}

internal sealed class LoginUiState {
    data object Initial : LoginUiState()
    data object LoginSuccess : LoginUiState()
    data object Loading : LoginUiState()
    data object Error : LoginUiState()
    data class LoadClientSuccess(val clientName: String?) : LoginUiState()
}
