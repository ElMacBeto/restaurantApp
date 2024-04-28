package com.humbjorch.restaurantapp.ui.orderRegister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.LoaderNBEXWidget
import com.humbjorch.restaurantapp.core.utils.alerts.CustomToastWidget
import com.humbjorch.restaurantapp.core.utils.alerts.TypeToast
import com.humbjorch.restaurantapp.core.utils.extDismissLoader
import com.humbjorch.restaurantapp.core.utils.extShowLoader
import com.humbjorch.restaurantapp.core.utils.parcelable
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.data.model.ProductsOrderModel
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
        imgBtn = findViewById(R.id.back_btn)

        setOrderValues()
        setStartDestination()
        setListeners()
    }

    private fun setListeners() {
        imgBtn.setOnClickListener {
            if (navController.currentDestination!!.id == R.id.orderTypeSelectionFragment2)
                finish()
            else
                navController.popBackStack()
        }
    }

    private fun setStartDestination() {
        if (activityViewModel.order.value == null) //is new register
            return
        if ((activityViewModel.tableSelected.value?.toInt() ?: 0) < 0)  //is delivery order
            return

        val navGraph = navController.navInflater.inflate(R.navigation.register_order_navigation)
        navGraph.setStartDestination(R.id.newOrderSelectionFragment)
        navController.graph = navGraph
    }

    private fun setOrderValues() {
        activityViewModel.tableSelected.value = intent.getIntExtra(TABLE_POSITION_EXTRA_KEY, 0)
        activityViewModel.order.value = intent.parcelable(ORDER_EXTRA_KEY)
        activityViewModel.orderAddress.value = intent.getStringExtra(ORDER_ADDRESS_EXTRA_KEY) ?: ""
        activityViewModel.productList.value =
            activityViewModel.order.value?.productList ?: emptyList()
        activityViewModel.isEditOrder = activityViewModel.order.value != null
    }

    fun showLoader() {
        extShowLoader(loader)
    }

    fun dismissLoader() {
        extDismissLoader(loader)
    }

    companion object {
        const val TABLE_POSITION_EXTRA_KEY = "table_position"
        const val ORDER_EXTRA_KEY = "order"
        const val ORDER_ADDRESS_EXTRA_KEY = "order_address"
    }

}