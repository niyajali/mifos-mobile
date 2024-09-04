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
import org.mifos.mobile.core.data.repositoryImpl.BeneficiaryRepositoryImp
import org.mifos.mobile.core.model.entity.beneficiary.Beneficiary
import org.mifos.mobile.core.model.entity.beneficiary.BeneficiaryPayload
import org.mifos.mobile.core.model.entity.beneficiary.BeneficiaryUpdatePayload
import org.mifos.mobile.core.model.entity.templates.beneficiary.BeneficiaryTemplate
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
class BeneficiaryRepositoryImpTest {

    @get:Rule
    val coroutineTestRule = MainDispatcherRule()

    @Mock
    lateinit var dataManager: DataManager

    private lateinit var beneficiaryRepositoryImp: BeneficiaryRepositoryImp

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        beneficiaryRepositoryImp = BeneficiaryRepositoryImp(dataManager)
    }

    @Test
    fun testBeneficiaryTemplate_Successful() = runTest {
        val success = mock(BeneficiaryTemplate::class.java)

        `when`(dataManager.beneficiaryTemplate())
            .thenReturn(success)

        val resultFlow = beneficiaryRepositoryImp.beneficiaryTemplate()
        resultFlow.test {
            assertEquals(success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).beneficiaryTemplate()
    }

    @Test(expected = Exception::class)
    fun testBeneficiaryTemplate_Unsuccessful() = runTest {
        `when`(dataManager.beneficiaryTemplate())
            .thenThrow(Exception("Error occurred"))
        val resultFlow = beneficiaryRepositoryImp.beneficiaryTemplate()
        resultFlow.test {
            assert(Throwable("Error occurred") == awaitError())
        }
        verify(dataManager).beneficiaryTemplate()
    }

    @Test
    fun testCreateBeneficiary_Successful() = runTest {
        val success = mock(ResponseBody::class.java)

        val beneficiaryPayload = mock(BeneficiaryPayload::class.java)

        `when`(dataManager.createBeneficiary(beneficiaryPayload))
            .thenReturn(success)

        val resultFlow = beneficiaryRepositoryImp.createBeneficiary(beneficiaryPayload)
        resultFlow.test {
            assertEquals(success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).createBeneficiary(beneficiaryPayload)
    }

    @Test(expected = Exception::class)
    fun testCreateBeneficiary_Unsuccessful() = runTest {
        val beneficiaryPayload = mock(BeneficiaryPayload::class.java)

        `when`(dataManager.createBeneficiary(beneficiaryPayload)).thenThrow(Exception("Error occurred"))

        val resultFlow = beneficiaryRepositoryImp.createBeneficiary(beneficiaryPayload)
        resultFlow.test {
            assert(Throwable("Error occurred") == awaitError())
            verify(dataManager).createBeneficiary(beneficiaryPayload)
        }

        @Test
        fun testUpdateBeneficiary_Successful() = runTest {
            val success = mock(ResponseBody::class.java)

            val beneficiaryUpdatePayload = mock(BeneficiaryUpdatePayload::class.java)

            `when`(dataManager.updateBeneficiary(123L, beneficiaryUpdatePayload))
                .thenReturn(success)

            val resultFlow =
                beneficiaryRepositoryImp.updateBeneficiary(123L, beneficiaryUpdatePayload)
            resultFlow.test {
                assertEquals(success, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            verify(dataManager).updateBeneficiary(123L, beneficiaryUpdatePayload)
        }

        @Test
        fun testUpdateBeneficiary_Unsuccessful() = runTest {
            val beneficiaryUpdatePayload = mock(BeneficiaryUpdatePayload::class.java)

            `when`(dataManager.updateBeneficiary(123L, beneficiaryUpdatePayload)).thenThrow(
                Exception("Error occurred"),
            )

            val result = beneficiaryRepositoryImp.updateBeneficiary(123L, beneficiaryUpdatePayload)
            result.test {
                assert(Throwable("Error occurred") == awaitError())
            }
        }

        @Test
        fun testDeleteBeneficiary_Successful() = runTest {
            val success = mock(ResponseBody::class.java)

            `when`(dataManager.deleteBeneficiary(123L)).thenReturn(success)

            val resultFlow = beneficiaryRepositoryImp.deleteBeneficiary(123L)
            resultFlow.test {
                assertEquals(success, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            verify(dataManager).deleteBeneficiary(123L)
        }

        @Test
        fun testDeleteBeneficiary_Unsuccessful() = runTest {
            `when`(dataManager.deleteBeneficiary(123L))
                .thenThrow(Exception("Error occurred"))
            val result = beneficiaryRepositoryImp.deleteBeneficiary(123L)
            result.test {
                assert(Throwable("Error occurred") == awaitError())
            }
        }

        @Test
        fun testBeneficiaryList_Successful() = runTest {
            val success = List(5) { mock(Beneficiary::class.java) }
            `when`(dataManager.beneficiaryList()).thenReturn(success)
            val resultFlow = beneficiaryRepositoryImp.beneficiaryList()
            resultFlow.test {
                assertEquals(success, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            verify(dataManager).beneficiaryList()
        }

        @Test
        fun testBeneficiaryList_Unsuccessful() = runTest {
            `when`(dataManager.beneficiaryList())
                .thenThrow(Exception("Error occurred"))
            val result = beneficiaryRepositoryImp.beneficiaryList()
            result.test {
                assert(Throwable("Error occurred") == awaitError())
            }
        }
    }
}
