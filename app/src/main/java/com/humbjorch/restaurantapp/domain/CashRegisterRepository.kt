package com.humbjorch.restaurantapp.domain

import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.data.datasource.remote.response.CashRegisterResponse
import com.humbjorch.restaurantapp.data.datasource.remote.webDb.CashRegisterWebDB
import com.humbjorch.restaurantapp.data.model.CashRegisterModel
import javax.inject.Inject

class CashRegisterRepository @Inject constructor(
    private val cashRegisterWebDB: CashRegisterWebDB
) {
    suspend fun getIncomesAndExpenses(date: String): Resource<List<CashRegisterResponse?>> {
        val incomesResponse = cashRegisterWebDB.getIncomes(date)
        val expensesResponse = cashRegisterWebDB.getExpenses(date)

        val responses = listOf(
            incomesResponse,
            expensesResponse,
        )

        responses.forEach {
            if (it.status == Status.ERROR)
                return Resource.error(it.message)
        }
        val data = listOf(
            incomesResponse.data,
            expensesResponse.data
        )
        return Resource.success(data)
    }

    suspend fun updateIncomes(date: String, incomeList: List<CashRegisterModel>) =
        cashRegisterWebDB.saveIncomes(
            date,
            CashRegisterResponse(incomeList)
        )

    suspend fun updateExpenses(date: String, cashRegister: List<CashRegisterModel>) =
        cashRegisterWebDB.saveExpenses(
            date,
            CashRegisterResponse(cashRegister)
        )


}