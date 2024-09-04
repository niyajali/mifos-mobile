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
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mifos.mobile.core.data.repositoryImpl.GuarantorRepositoryImp
import org.mifos.mobile.core.model.entity.guarantor.GuarantorApplicationPayload
import org.mifos.mobile.core.model.entity.guarantor.GuarantorPayload
import org.mifos.mobile.core.model.entity.guarantor.GuarantorTemplatePayload
import org.mifos.mobile.core.network.DataManager
import org.mifos.mobile.core.testing.util.MainDispatcherRule
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class GuarantorRepositoryImpTest {

    @get:Rule
    val coroutineTestRule = MainDispatcherRule()

    @Mock
    private lateinit var dataManager: DataManager

    private lateinit var guarantorRepositoryImp: GuarantorRepositoryImp

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        guarantorRepositoryImp = GuarantorRepositoryImp(dataManager)
    }

    @Test
    fun testGetGuarantorTemplate_Successful() = runTest {
        val success = mock(GuarantorTemplatePayload::class.java)

        `when`(dataManager.getGuarantorTemplate(123L)).thenReturn(success)

        val result = guarantorRepositoryImp.getGuarantorTemplate(123L)

        result.test {
            assertEquals(success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).getGuarantorTemplate(123L)
    }

    @Test(expected = Exception::class)
    fun testGetGuarantorTemplate_Unsuccessful() = runTest {
        val error = Exception("error")

        `when`(dataManager.getGuarantorTemplate(123L)).thenThrow(error)

        val result = guarantorRepositoryImp.getGuarantorTemplate(123L)
        result.test {
            assertEquals(Throwable("error"), awaitError())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).getGuarantorTemplate(123L)
    }

    @Test
    fun testCreateGuarantor_Successful() = runTest {
        val success = mock(ResponseBody::class.java)
        val payload = mock(GuarantorApplicationPayload::class.java)

        `when`(dataManager.createGuarantor(123L, payload)).thenReturn(success)

        val result = guarantorRepositoryImp.createGuarantor(123L, payload)
        result.test {
            assertEquals(success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).createGuarantor(123L, payload)
    }

    @Test(expected = Exception::class)
    fun testCreateGuarantor_Unsuccessful() = runTest {
        val error = Exception("Error")
        val payload = mock(GuarantorApplicationPayload::class.java)

        `when`(dataManager.createGuarantor(123L, payload)).thenThrow(error)

        val result = guarantorRepositoryImp.createGuarantor(123L, payload)
        result.test {
            assertEquals(Throwable("Error"), awaitError())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).createGuarantor(123L, payload)
    }

    @Test
    fun testUpdateGuarantor_Successful() = runTest {
        val success = mock(ResponseBody::class.java)
        val payload = mock(GuarantorApplicationPayload::class.java)

        `when`(dataManager.updateGuarantor(payload, 11L, 22L)).thenReturn(success)

        val result = guarantorRepositoryImp.updateGuarantor(payload, 11L, 22L)
        result.test {
            assertEquals(success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).updateGuarantor(payload, 11L, 22L)
    }

    @Test(expected = Exception::class)
    fun testUpdateGuarantor_Unsuccessful() = runTest {
        val error = Exception("Error")
        val payload = mock(GuarantorApplicationPayload::class.java)

        `when`(dataManager.updateGuarantor(payload, 11L, 22L)).thenThrow(error)

        val result = guarantorRepositoryImp.updateGuarantor(payload, 11L, 22L)
        result.test {
            assertEquals(Throwable("Error"), awaitError())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).updateGuarantor(payload, 11L, 22L)
    }

    @Test
    fun testDeleteGuarantor_Successful() = runTest {
        val success = mock(ResponseBody::class.java)
        `when`(dataManager.deleteGuarantor(1L, 2L)).thenReturn(success)

        val result = guarantorRepositoryImp.deleteGuarantor(1L, 2L)
        result.test {
            assertEquals(success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).deleteGuarantor(1L, 2L)
    }

    @Test(expected = Exception::class)
    fun testDeleteGuarantor_Unsuccessful() = runTest {
        val error = Exception("Error")

        `when`(dataManager.deleteGuarantor(1L, 2L)).thenThrow(error)

        val result = guarantorRepositoryImp.deleteGuarantor(1L, 2L)
        result.test {
            assertEquals(Throwable("Error"), awaitError())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).deleteGuarantor(1L, 2L)
    }

    @Test
    fun testGetGuarantorList_Successful() = runTest {
        val success = List(4) {
            mock(GuarantorPayload::class.java)
        }
        `when`(dataManager.getGuarantorList(123L)).thenReturn(success)

        val result = guarantorRepositoryImp.getGuarantorList(123L)
        result.test {
            assertEquals(success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).getGuarantorList(123L)
    }

    @Test(expected = Exception::class)
    fun testGetGuarantorList_Unsuccessful() = runTest {
        val error = Exception("Error")

        `when`(dataManager.getGuarantorList(123L)).thenThrow(error)

        val result = guarantorRepositoryImp.getGuarantorList(123L)
        result.test {
            assertEquals(Throwable("Error"), awaitError())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).getGuarantorList(123L)
    }
}
