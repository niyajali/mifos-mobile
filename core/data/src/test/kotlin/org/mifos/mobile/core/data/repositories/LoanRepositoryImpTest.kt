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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mifos.mobile.core.data.repositoryImpl.LoanRepositoryImp
import org.mifos.mobile.core.model.entity.accounts.loan.LoanWithAssociations
import org.mifos.mobile.core.model.entity.accounts.loan.LoanWithdraw
import org.mifos.mobile.core.model.entity.templates.loans.LoanTemplate
import org.mifos.mobile.core.network.DataManager
import org.mifos.mobile.core.testing.util.MainDispatcherRule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class LoanRepositoryImpTest {

    @get:Rule
    val coroutineTestRule = MainDispatcherRule()

    @Mock
    lateinit var dataManager: DataManager

    @Mock
    lateinit var loanWithdraw: LoanWithdraw

    private lateinit var loanRepositoryImp: LoanRepositoryImp

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        loanRepositoryImp = LoanRepositoryImp(dataManager)
    }

    @Test
    fun testGetLoanWithAssociations_Successful() = runTest {
        Dispatchers.setMain(Dispatchers.Unconfined)
        val success = mock(LoanWithAssociations::class.java)

        `when`(
            dataManager.getLoanWithAssociations(
                Mockito.anyString(),
                Mockito.anyLong(),
            ),
        ).thenReturn(success)

        val result = loanRepositoryImp.getLoanWithAssociations(
            "associationType",
            1,
        )
        result.test {
            assertEquals(success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).getLoanWithAssociations(Mockito.anyString(), Mockito.anyLong())
        Dispatchers.resetMain()
    }

    @Test(expected = Exception::class)
    fun testGetLoanWithAssociations_Unsuccessful() = runTest {
        Dispatchers.setMain(Dispatchers.Unconfined)
        `when`(
            dataManager.getLoanWithAssociations(
                Mockito.anyString(),
                Mockito.anyLong(),
            ),
        ).thenThrow(Exception("Error occurred"))
        val result = loanRepositoryImp.getLoanWithAssociations(
            "associationType",
            1,
        )
        result!!.test {
            assert(Throwable("Error occurred") == awaitError())
        }
        verify(dataManager).getLoanWithAssociations(Mockito.anyString(), Mockito.anyLong())
        Dispatchers.resetMain()
    }

    @Test
    fun testWithdrawLoanAccount_Successful() = runTest {
        Dispatchers.setMain(Dispatchers.Unconfined)
        val success = mock(ResponseBody::class.java)

        `when`(dataManager.withdrawLoanAccount(1, loanWithdraw)).thenReturn(success)

        val result = loanRepositoryImp.withdrawLoanAccount(1, loanWithdraw)
        result?.test {
            assertEquals(success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).withdrawLoanAccount(1, loanWithdraw)
    }

    @Test(expected = Exception::class)
    fun testWithdrawLoanAccount_Unsuccessful() = runTest {
        Dispatchers.setMain(Dispatchers.Unconfined)
        `when`(dataManager.withdrawLoanAccount(1, loanWithdraw))
            .thenThrow(Exception("Error occurred"))
        val result = loanRepositoryImp.withdrawLoanAccount(1, loanWithdraw)
        result!!.test {
            assert(Throwable("Error occurred") == awaitError())
        }
        verify(dataManager).withdrawLoanAccount(1, loanWithdraw)
        Dispatchers.resetMain()
    }

    @Test
    fun testTemplate_Successful() = runTest {
        Dispatchers.setMain(Dispatchers.Unconfined)
        val success = mock(LoanTemplate::class.java)
        `when`(dataManager.loanTemplate()).thenReturn(success)

        val result = loanRepositoryImp.template()

        result.test {
            assertEquals(success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).loanTemplate()
        Dispatchers.resetMain()
    }

    @Test(expected = Exception::class)
    fun testTemplate_Unsuccessful() = runTest {
        Dispatchers.setMain(Dispatchers.Unconfined)
        `when`(dataManager.loanTemplate())
            .thenThrow(Exception("Error occurred"))

        val result = loanRepositoryImp.template()
        result.test {
            assert(Throwable("Error occurred") == awaitError())
        }
        verify(dataManager).loanTemplate()
        Dispatchers.resetMain()
    }

    @Test
    fun testGetLoanTemplateByProduct_Successful() = runTest {
        Dispatchers.setMain(Dispatchers.Unconfined)
        val success = mock(LoanTemplate::class.java)
        `when`(dataManager.getLoanTemplateByProduct(1)).thenReturn(success)

        val result = loanRepositoryImp.getLoanTemplateByProduct(1)
        result.test {
            assertEquals(success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(dataManager).getLoanTemplateByProduct(1)
        Dispatchers.resetMain()
    }

    @Test(expected = Exception::class)
    fun testGetLoanTemplateByProduct_Unsuccessful() = runTest {
        Dispatchers.setMain(Dispatchers.Unconfined)
        val error: Response<LoanTemplate?> =
            Response.error(404, ResponseBody.create(null, "error"))
        `when`(dataManager.getLoanTemplateByProduct(1))
            .thenThrow(Exception("Error occurred"))

        val result = loanRepositoryImp.getLoanTemplateByProduct(1)
        result.test {
            assert(Throwable("Error occurred") == awaitError())
        }
        verify(dataManager).getLoanTemplateByProduct(1)
        Dispatchers.resetMain()
    }
}
