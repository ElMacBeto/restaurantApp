package com.humbjorch.restaurantapp.ui.cashRegister

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.OrderStatus
import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.core.utils.Tools
import com.humbjorch.restaurantapp.core.utils.Tools.formatMoney
import com.humbjorch.restaurantapp.core.utils.alerts.CustomToastWidget
import com.humbjorch.restaurantapp.core.utils.alerts.TypeToast
import com.humbjorch.restaurantapp.core.utils.genericAlert
import com.humbjorch.restaurantapp.data.model.CashRegisterModel
import com.humbjorch.restaurantapp.data.model.OrderListModel
import com.humbjorch.restaurantapp.databinding.FragmentCashRegisterBinding
import com.humbjorch.restaurantapp.ui.cashRegister.adapter.CashRegisterAdapter
import com.humbjorch.restaurantapp.ui.cashRegister.dialog.CashRegisterDialog
import com.humbjorch.restaurantapp.ui.cashRegister.dialog.CashRegisterType
import com.humbjorch.restaurantapp.ui.home.NewHomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CashRegisterFragment : Fragment() {

    private val viewModel: CashRegisterViewModel by viewModels()
    private lateinit var binding: FragmentCashRegisterBinding
    private lateinit var incomeAdapter: CashRegisterAdapter
    private lateinit var expensesAdapter: CashRegisterAdapter
    private var incomeList = listOf<CashRegisterModel>()
    private var expensesList = listOf<CashRegisterModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCashRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setAdapters()
        setObservers()
        viewModel.getCurrentDayOrders()
        binding.tvDate.text = Tools.getCurrentDate(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListeners() {
        binding.btnAddIncome.setOnClickListener {
            val dialog = CashRegisterDialog(CashRegisterType.INCOME) {
                incomeList = incomeList.plus(it)
                viewModel.updateIncomes(incomeList)
            }
            dialog.show(parentFragmentManager, CashRegisterDialog.TAG)
        }
        binding.btnAddExpenses.setOnClickListener {
            val dialog = CashRegisterDialog(CashRegisterType.EXPENSES) {
                expensesList = expensesList.plus(it)
                viewModel.updateExpenses(expensesList)
            }
            dialog.show(parentFragmentManager, CashRegisterDialog.TAG)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setObservers() {
        viewModel.getAllOrdersLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    (activity as NewHomeActivity).showLoader()
                }

                Status.SUCCESS -> {
                    (activity as NewHomeActivity).dismissLoader()
                    val orders = it.data ?: OrderListModel()
                    getTotalOfDay(orders)
                    viewModel.getIncomeAndExpenses()
                }

                Status.ERROR -> {
                    (activity as NewHomeActivity).dismissLoader()
                    CustomToastWidget.show(
                        activity = requireActivity(),
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                }
            }
        }

        viewModel.incomesAndExpensesLiveData.observe(viewLifecycleOwner){
            when (it.status) {
                Status.LOADING -> {
                    (activity as NewHomeActivity).showLoader()
                }

                Status.SUCCESS -> {
                    (activity as NewHomeActivity).dismissLoader()
                    val incomes = it.data?.get(0)?.list ?: emptyList()
                    val expenses = it.data?.get(1)?.list ?: emptyList()

                    updateIncomesAndExpensesList(incomes, expenses)
                }

                Status.ERROR -> {
                    (activity as NewHomeActivity).dismissLoader()
                    CustomToastWidget.show(
                        activity = requireActivity(),
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                }
            }
        }

        viewModel.updateIncomesLiveData.observe(viewLifecycleOwner){
            when (it.status) {
                Status.LOADING -> {
                    (activity as NewHomeActivity).showLoader()
                }

                Status.SUCCESS -> {
                    incomeAdapter.updateList(incomeList)
                    updateTotals()
                }

                Status.ERROR -> {
                    (activity as NewHomeActivity).dismissLoader()
                    CustomToastWidget.show(
                        activity = requireActivity(),
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                }
            }
        }

        viewModel.updateExpensesLiveData.observe(viewLifecycleOwner){
            when (it.status) {
                Status.LOADING -> {
                    (activity as NewHomeActivity).showLoader()
                }

                Status.SUCCESS -> {
                    expensesAdapter.updateList(expensesList)
                    updateTotals()
                }

                Status.ERROR -> {
                    (activity as NewHomeActivity).dismissLoader()
                    CustomToastWidget.show(
                        activity = requireActivity(),
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setAdapters() {
        incomeAdapter = CashRegisterAdapter(
            dataSet = incomeList,
            onClickEdit = { initCashRegister ->
                val dialog = CashRegisterDialog(
                    CashRegisterType.INCOME,
                    initCashRegister
                ) { newCashRegister ->
                    incomeList.onEach {
                        if (it.name == newCashRegister.name)
                            it.value = newCashRegister.value
                    }
                    viewModel.updateIncomes(incomeList)
                }
                dialog.show(parentFragmentManager, CashRegisterDialog.TAG)
            },
            onClickDelete = {
                (activity as NewHomeActivity).genericAlert(
                    titleAlert = getString(R.string.dialog_title_delete_cash_register),
                    descriptionAlert = getString(R.string.dialog_description_delete_cash_register),
                    txtBtnNegativeAlert = getString(R.string.dialog_cancel_button),
                    txtBtnPositiveAlert = getString(R.string.dialog_positive_button),
                    buttonPositiveAction = {
                        incomeList = incomeList.minus(it)
                        viewModel.updateIncomes(incomeList)
                    },
                    buttonNegativeAction = { }
                )
            }
        )

        binding.rvIncomes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIncomes.adapter = incomeAdapter

        //------------------------------------------------------------------------------------------

        expensesAdapter = CashRegisterAdapter(
            dataSet = expensesList,
            onClickEdit = { initCashRegister ->
                val dialog = CashRegisterDialog(
                    CashRegisterType.EXPENSES,
                    initCashRegister
                ) { newCashRegister ->
                    expensesList.onEach {
                        if (it.name == newCashRegister.name)
                            it.value = newCashRegister.value
                    }
                    viewModel.updateExpenses(expensesList)
                }
                dialog.show(parentFragmentManager, CashRegisterDialog.TAG)
            },
            onClickDelete = {
                (activity as NewHomeActivity).genericAlert(
                    titleAlert = getString(R.string.dialog_title_delete_cash_register),
                    descriptionAlert = getString(R.string.dialog_description_delete_cash_register),
                    txtBtnNegativeAlert = getString(R.string.dialog_cancel_button),
                    txtBtnPositiveAlert = getString(R.string.dialog_positive_button),
                    buttonPositiveAction = {
                        expensesList = expensesList.minus(it)
                        viewModel.updateExpenses(expensesList)
                    },
                    buttonNegativeAction = { }
                )
            }
        )

        binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpenses.adapter = expensesAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateIncomesAndExpensesList(
        incomes: List<CashRegisterModel>,
        expenses: List<CashRegisterModel>
    ) {

        incomes.forEach {
            if(it.name !in listOf("Pago efectivo", "Pago tarjeta")){
                incomeList = incomeList.plus(it)
            }
        }

        expenses.forEach {
            expensesList = expensesList.plus(it)
        }

        incomeAdapter.updateList(incomeList)
        expensesAdapter.updateList(expensesList)
        updateTotals()
        viewModel.updateIncomes(incomeList)
    }

    private fun getTotalOfDay(orderDay: OrderListModel) {
        var cashTotal = 0
        var cardTotal = 0

        orderDay.orders.forEach { order ->
            if (order.status == OrderStatus.PAID.value || order.status == OrderStatus.CASH_PAYMENT.value)
                cashTotal += Tools.getTotal(order)
            if (order.status == OrderStatus.CARD_PAYMENT.value)
                cardTotal += Tools.getTotal(order)
        }

        incomeList = incomeList.plus(
            CashRegisterModel(
                name = "Pago efectivo",
                value = cashTotal,
                isEditable = false
            )
        )
        incomeList = incomeList.plus(
            CashRegisterModel(
                name = "Pago tarjeta",
                value = cardTotal,
                isEditable = false
            )
        )
        incomeAdapter.updateList(incomeList)
        updateTotals()
    }

    private fun updateTotals() {
        val incomeTotal = incomeList.sumOf { it.value }
        binding.tvTotalIncome.text =
            getString(R.string.label_total_income, formatMoney(incomeTotal.toDouble()))

        val expensesTotal = expensesList.sumOf { it.value }
        binding.tvTotalExpenses.text =
            getString(R.string.label_total_expenses, formatMoney(expensesTotal.toDouble()))

        val total = incomeTotal - expensesTotal
        binding.tvTotal.text =
            getString(R.string.label_total_expenses, formatMoney(total.toDouble()))
    }
}