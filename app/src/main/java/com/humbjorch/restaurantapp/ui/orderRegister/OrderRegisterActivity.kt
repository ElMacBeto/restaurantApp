package com.humbjorch.restaurantapp.ui.orderRegister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.LoaderNBEXWidget
import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.core.utils.alerts.CustomToastWidget
import com.humbjorch.restaurantapp.core.utils.alerts.TypeToast
import com.humbjorch.restaurantapp.core.utils.extDismissLoader
import com.humbjorch.restaurantapp.core.utils.extShowLoader
import com.humbjorch.restaurantapp.core.utils.parcelable
import com.humbjorch.restaurantapp.data.model.OrderModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderRegisterActivity : AppCompatActivity() {

    private val activityViewModel: RegisterOrderViewModel by viewModels()
    private lateinit var navController: NavController
    private val loader by lazy { LoaderNBEXWidget() }
    private lateinit var imgBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_register)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        setObservers()
        setListeners()
        setOrderValues()
        activityViewModel.setTables()
    }

    private fun setObservers() {
        activityViewModel.liveDataSetTables.observe(this){
            when (it.status) {
                Status.LOADING -> {
                    showLoader()
                }
                Status.SUCCESS -> {
                    dismissLoader()
                    setStartDestination()
                }
                Status.ERROR -> {
                    dismissLoader()
                    CustomToastWidget.show(
                        activity = this,
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                }
            }
        }
    }

    private fun setListeners() {
        imgBtn = findViewById(R.id.back_btn)
        imgBtn.setOnClickListener {
            if (navController.currentDestination!!.id == R.id.orderTypeSelectionFragment2)
                finish()
            else
                navController.popBackStack()
        }
    }

    private fun setStartDestination() {
        if (activityViewModel.order == null)        //is new register
            return
        if ((activityViewModel.tableSelected) < 0)  //is delivery order
            return

        val navGraph = navController.navInflater.inflate(R.navigation.register_order_navigation)
        navGraph.setStartDestination(R.id.newOrderSelectionFragment)
        navController.graph = navGraph
    }

    private fun setOrderValues() {
        val order =  intent.parcelable<OrderModel>(ORDER_EXTRA_KEY)
        activityViewModel.order = order
        activityViewModel.tableSelected = order?.table?.toInt() ?: 0
        activityViewModel.orderAddress = order?.address ?: ""
        activityViewModel.productList.value = order?.productList ?: emptyList()
        activityViewModel.isEditOrder = activityViewModel.order != null
    }

    fun showLoader() {
        extShowLoader(loader)
    }

    fun dismissLoader() {
        extDismissLoader(loader)
    }

    companion object {
        const val ORDER_EXTRA_KEY = "order"
    }

}