package com.humbjorch.restaurantapp.ui.cashRegister

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humbjorch.restaurantapp.core.utils.Tools
import com.humbjorch.restaurantapp.data.datasource.remote.Resource
import com.humbjorch.restaurantapp.data.datasource.remote.response.CashRegisterResponse
import com.humbjorch.restaurantapp.data.model.CashRegisterModel
import com.humbjorch.restaurantapp.data.model.OrderListModel
import com.humbjorch.restaurantapp.domain.CashRegisterRepository
import com.humbjorch.restaurantapp.domain.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CashRegisterViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val cashRegisterRepository: CashRegisterRepository
) : ViewModel() {

    private var _getAllOrdersLiveData = MutableLiveData<Resource<OrderListModel?>>()
    val getAllOrdersLiveData: LiveData<Resource<OrderListModel?>> get() = _getAllOrdersLiveData

    private var _incomesAndExpensesLiveData =
        MutableLiveData<Resource<List<CashRegisterResponse?>>>()
    val incomesAndExpensesLiveData: LiveData<Resource<List<CashRegisterResponse?>>> get() = _incomesAndExpensesLiveData

    private var _updateIncomesLiveData =
        MutableLiveData<Resource<Boolean>>()
    val updateIncomesLiveData: LiveData<Resource<Boolean>> get() = _updateIncomesLiveData

    private var _updateExpensesLiveData =
        MutableLiveData<Resource<Boolean>>()
    val updateExpensesLiveData: LiveData<Resource<Boolean>> get() = _updateExpensesLiveData

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDayOrders() {
        viewModelScope.launch {
            _getAllOrdersLiveData.value = Resource.loading()
            val currentDate = Tools.getCurrentDate()
            _getAllOrdersLiveData.value = productsRepository.getOrders(currentDate)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getIncomeAndExpenses() {
        viewModelScope.launch {
            _incomesAndExpensesLiveData.value =
                cashRegisterRepository.getIncomesAndExpenses(
                    Tools.getCurrentDate()
                )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateIncomes(incomeList: List<CashRegisterModel>) {
        viewModelScope.launch {
            _updateIncomesLiveData.value =
                cashRegisterRepository.updateIncomes(Tools.getCurrentDate(), incomeList)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateExpenses(expensesList: List<CashRegisterModel>) {
        viewModelScope.launch {
            _updateExpensesLiveData.value =
                cashRegisterRepository.updateExpenses(Tools.getCurrentDate(), expensesList)
        }
    }


}