package com.humbjorch.restaurantapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.LoaderNBEXWidget
import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.core.utils.alerts.CustomToastWidget
import com.humbjorch.restaurantapp.core.utils.alerts.TypeToast
import com.humbjorch.restaurantapp.core.utils.extDismissLoader
import com.humbjorch.restaurantapp.core.utils.extShowLoader
import com.humbjorch.restaurantapp.ui.home.NewHomeActivity
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()
    private val loader by lazy { LoaderNBEXWidget() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setObservers()
        viewModel.setAllProducts()
    }

    private fun setObservers() {
        viewModel.setProductsLiveData.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                   this@SplashActivity.showLoader()
                }

                Status.SUCCESS -> {
                   dismissLoader()
                    Handler(Looper.getMainLooper()).postDelayed({
                        viewModel.setPrinterSettings()
                        val intent = Intent(this, NewHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 1500)
                }

                Status.ERROR -> {
                    this.dismissLoader()
                    CustomToastWidget.show(
                        activity = this,
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                    finish()
                }
            }
        }
    }

    private fun showLoader() {
        extShowLoader(loader)
    }

    private fun dismissLoader() {
        extDismissLoader(loader)
    }
}