/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.core.data.repositories

import app.cash.turbine.test
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mifos.mobile.core.data.repositoryImpl.ClientChargeRepositoryImp
import org.mifos.mobile.core.datastore.model.Charge
import org.mifos.mobile.core.model.entity.Page
import org.mifos.mobile.core.network.DataManager
import org.mifos.mobile.core.testing.util.MainDispatcherRule
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class ClientChargeRepositoryImpTest {

    @get:Rule
    val coroutineTestRule = MainDispatcherRule()

    @Mock
    lateinit var dataManager: DataManager

    private lateinit var clientChargeRepositoryImp: ClientChargeRepositoryImp

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        clientChargeRepositoryImp = ClientChargeRepositoryImp(dataManager)
    }

    @Test
    fun testGetClientCharges_Successful() = runTest {
        val clientChargeMock = List(5) { mock(Charge::class.java) }
        val chargeList = clientChargeMock.toList()
        val success = Page<Charge>(5, chargeList)
        `when`(dataManager.getClientCharges(123L))
            .thenReturn(success)
        val resultFlow = clientChargeRepositoryImp.getClientCharges(123L)
        resultFlow.test {
            assertEquals(success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test(expected = Exception::class)
    fun testGetClientCharges_Unsuccessful() = runTest {
        `when`(dataManager.getClientCharges(123L))
            .thenThrow(Exception("Error occurred"))
        val result = clientChargeRepositoryImp.getClientCharges(123L)
        result.test {
            assert(Throwable("Error occurred") == awaitError())
        }
    }

    @Test
    fun testGetLoanCharges_Successful() = runTest {
        val loanChargeMock = mock(Charge::class.java)
        val success = List(5) { loanChargeMock }.toList()
        `when`(dataManager.getLoanCharges(123L)).thenReturn(success)
        val resultFlow = clientChargeRepositoryImp.getLoanCharges(123L)
        resultFlow.test {
            assertEquals(success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test(expected = Exception::class)
    fun testGetLoanCharges_Unsuccessful() = runTest {
        `when`(dataManager.getLoanCharges(123L))
            .thenThrow(Exception("Error occurred"))
        val result = clientChargeRepositoryImp.getLoanCharges(123L)
        result.test {
            assert(Throwable("Error occurred") == awaitError())
        }
    }

    @Test
    fun testGetSavingsCharges_Successful() = runTest {
        val savingChargeMock = mock(Charge::class.java)
        val success = List(5) { savingChargeMock }.toList()
        `when`(dataManager.getSavingsCharges(123L)).thenReturn(success)
        val resultFlow = clientChargeRepositoryImp.getSavingsCharges(123L)
        resultFlow.test {
            assertEquals(success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test(expected = Exception::class)
    fun testGetSavingsCharges_Unsuccessful() = runTest {
        `when`(dataManager.getSavingsCharges(123L))
            .thenThrow(Exception("Error occurred"))
        val result = clientChargeRepositoryImp.getSavingsCharges(123L)
        result.test {
            assert(Throwable("Error occurred") == awaitError())
        }
    }

    @Test
    fun testClientLocalCharges_Successful() = runTest {
        val clientLocalChargeMock = List(5) { mock(Charge::class.java) }
        val chargeList = clientLocalChargeMock.toList()
        val success = Page<Charge?>(5, chargeList)
        `when`(dataManager.clientLocalCharges()).thenReturn(success)
        val resultFlow = clientChargeRepositoryImp.clientLocalCharges()
        resultFlow.test {
            assertEquals(success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test(expected = Exception::class)
    fun testClientLocalCharges_Unsuccessful() = runTest {
        `when`(dataManager.clientLocalCharges())
            .thenThrow(Exception("Error occurred"))
        val result = clientChargeRepositoryImp.clientLocalCharges()
        result.test {
            assert(Throwable("Error occurred") == awaitError())
        }
    }
}
