package com.humbjorch.restaurantapp.data.datasource.remote.webDb

import com.humbjorch.restaurantapp.data.datasource.remote.api.FirebaseApiService
import com.humbjorch.restaurantapp.data.datasource.remote.response.CashRegisterResponse
import javax.inject.Inject

class CashRegisterWebDB @Inject constructor(private val service: FirebaseApiService) {

    suspend fun getIncomes(date:String) = service.getIncomes(date)

    suspend fun getExpenses(date:String) = service.getExpenses(date)

    suspend fun saveIncomes(date: String, cashRegister: CashRegisterResponse) =
        service.saveIncomes(date, cashRegister)

    suspend fun saveExpenses(date: String, cashRegister: CashRegisterResponse) =
        service.saveExpenses(date, cashRegister)

}