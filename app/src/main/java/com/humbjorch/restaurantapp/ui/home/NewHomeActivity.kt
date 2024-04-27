package com.humbjorch.restaurantapp.ui.home

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.humbjorch.restaurantapp.core.utils.LoaderNBEXWidget
import com.humbjorch.restaurantapp.core.utils.extDismissLoader
import com.humbjorch.restaurantapp.core.utils.extShowLoader
import com.humbjorch.restaurantapp.databinding.ActivityNewHomeBinding
import com.humbjorch.restaurantapp.ui.orderRegister.OrderRegisterActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewHomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewHomeBinding
    private val loader by lazy { LoaderNBEXWidget() }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityNewHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }

    private fun setListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, OrderRegisterActivity::class.java))
        }
    }

    fun showLoader() {
        extShowLoader(loader)
    }

    fun dismissLoader() {
        extDismissLoader(loader)
    }

    fun isEnableSwitch(value: Boolean){
        binding.containerTopToolbar.swDelivery.isEnabled = value
    }

}