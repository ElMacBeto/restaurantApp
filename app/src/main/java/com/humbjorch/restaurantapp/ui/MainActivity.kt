package com.humbjorch.restaurantapp.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.LoaderNBEXWidget
import com.humbjorch.restaurantapp.core.utils.Tools
import com.humbjorch.restaurantapp.core.utils.alerts.GenericDialog
import com.humbjorch.restaurantapp.core.utils.showHide
import com.humbjorch.restaurantapp.databinding.ActivityMainBinding
import com.humbjorch.restaurantapp.ui.home.HomeFragmentDirections
import com.humbjorch.restaurantapp.ui.settings.SettingsFragmentDirections
import com.humbjorch.restaurantapp.ui.summary.SummaryFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val loader by lazy { LoaderNBEXWidget() }
    private lateinit var navController: NavController
    private var settingNav: (() -> Unit)? = null
    private var homeNav: (() -> Unit)? = null
    private var summaryNav: (() -> Unit)? = null

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val currentDestination = navController.currentDestination
            when (currentDestination?.id) {
                R.id.splashFragment -> {
                    finish()
                }

                R.id.homeFragment -> {
                    genericAlert(
                        titleAlert = getString(R.string.dialog_title_confirmation),
                        descriptionAlert = getString(R.string.dialog_description_finish_app),
                        txtBtnNegativeAlert = getString(R.string.dialog_cancel_button),
                        txtBtnPositiveAlert = getString(R.string.dialog_positive_button),
                        buttonPositiveAction = { finish() },
                        buttonNegativeAction = { }
                    )
                }

                else -> navController.navigateUp()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.fragmentContainer.id) as NavHostFragment
        navController = navHostFragment.navController
        onBackPressedDispatcher.addCallback(this, callback)
        setListeners()
        setNavigationChange()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setNavigationChange() {
        navController.addOnDestinationChangedListener { _, fragment, _ ->
            when (fragment.id) {
                R.id.splashFragment -> {
                    setTopToolbar(
                        showMenu = false
                    )
                }

                R.id.homeFragment -> {
                    setTopToolbar(
                        showMenu = true,
                        title = R.string.top_toolbar_home_title,
                        showSwitch = true
                    )
                    showLateralNavigation(true)
                    settingNav = {
                        val action = HomeFragmentDirections.actionHomeFragmentToSettingsFragment()
                        navController.navigate(action)
                    }
                    homeNav = null
                    summaryNav = {
                        val action = HomeFragmentDirections.actionHomeFragmentToSummaryFragment()
                        navController.navigate(action)
                    }
                }

                R.id.orderTypeSelectionFragment -> {
                    setTopToolbar(
                        showMenu = true,
                        title = R.string.top_toolbar_new_order_title,
                        showSwitch = false
                    )
                    showLateralNavigation(false)
                }

                R.id.orderSectionFragment -> {
                    setTopToolbar(
                        showMenu = true,
                        title = R.string.top_toolbar_new_order_title,
                        showSwitch = false
                    )
                    showLateralNavigation(false)
                }

                R.id.settingsFragment -> {
                    setTopToolbar(
                        showMenu = true,
                        title = R.string.top_toolbar_settings_title,
                        showSwitch = false
                    )
                    showLateralNavigation(true)
                    settingNav = null
                    homeNav = {
                        val action = SettingsFragmentDirections.actionSettingsFragmentToHomeFragment()
                        navController.navigate(action)
                    }
                    summaryNav = {
                        val action = SettingsFragmentDirections.actionSettingsFragmentToSummaryFragment()
                        navController.navigate(action)
                    }
                }
                R.id.summaryFragment -> {
                    setTopToolbar(
                        showMenu = true,
                        title = R.string.label_title_summary,
                        showSwitch = false
                    )
                    showLateralNavigation(true)
                    settingNav = {
                        val action = SummaryFragmentDirections.actionSummaryFragmentToSettingsFragment()
                        navController.navigate(action)
                    }
                    homeNav = {
                        val action = SummaryFragmentDirections.actionSummaryFragmentToHomeFragment()
                        navController.navigate(action)
                    }
                    summaryNav = null
                }
            }
        }
    }

    private fun setListeners() {
        binding.navBar.btnSettings.setOnClickListener {
            settingNav?.invoke()

            val colorFromResource = ContextCompat.getColor(this, R.color.blue2)
            val colorSelected = ColorStateList.valueOf(colorFromResource)
            binding.navBar.btnSettings.backgroundTintList = colorSelected

            val colorStateList = ColorStateList.valueOf(Color.WHITE)
            binding.navBar.btnHome.backgroundTintList = colorStateList
            binding.navBar.btnInventory.backgroundTintList = colorStateList
        }
        binding.navBar.btnHome.setOnClickListener {
            homeNav?.invoke()

            val colorFromResource = ContextCompat.getColor(this, R.color.blue2)
            val colorSelected = ColorStateList.valueOf(colorFromResource)
            binding.navBar.btnHome.backgroundTintList = colorSelected

            val colorStateList = ColorStateList.valueOf(Color.WHITE)
            binding.navBar.btnSettings.backgroundTintList = colorStateList
            binding.navBar.btnInventory.backgroundTintList = colorStateList
        }
        binding.navBar.btnInventory.setOnClickListener {
            summaryNav?.invoke()

            val colorFromResource = ContextCompat.getColor(this, R.color.blue2)
            val colorSelected = ColorStateList.valueOf(colorFromResource)
            binding.navBar.btnInventory.backgroundTintList = colorSelected

            val colorStateList = ColorStateList.valueOf(Color.WHITE)
            binding.navBar.btnHome.backgroundTintList = colorStateList
            binding.navBar.btnSettings.backgroundTintList = colorStateList
        }
    }

    fun showLoader() {
        try {
            val loaderDialog = supportFragmentManager.findFragmentByTag("Loader")
            val isShowing = loader.dialog?.isShowing ?: false
            if (loaderDialog != null && loaderDialog.isAdded) {
                return
            }

            if (!loader.isAdded && !loader.isVisible && !isShowing) {
                loader.show(supportFragmentManager, "Loader")
                supportFragmentManager.executePendingTransactions()
            }
        } catch (e: Exception) {
            //ERROR
        }
    }

    fun dismissLoader() {
        if (loader.isAdded) {
            if (loader.isResumed) {
                loader.dismiss()
            } else {
                loader.dismissAllowingStateLoss()
            }
        }
    }

    fun genericAlert(
        imageAlert: Int = R.drawable.generic_icon_warning,
        titleAlert: String,
        descriptionAlert: String,
        txtBtnPositiveAlert: String,
        txtBtnNegativeAlert: String,
        isCancelableAlert: Boolean = false,
        buttonPositiveAction: (() -> Unit)? = null,
        buttonNegativeAction: (() -> Unit)? = null,
    ) {
        lifecycleScope.launchWhenResumed {
            GenericDialog().apply {
                imgDialog = imageAlert
                txtConfirm = txtBtnPositiveAlert
                txtCancel = txtBtnNegativeAlert
                txtTitle = titleAlert
                txtMessageAlert = descriptionAlert
                isCancelable = isCancelableAlert
                listener = object : GenericDialog.OnClickListener {
                    override fun onClick(which: Int) {
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                buttonPositiveAction?.invoke()
                            }

                            else -> {
                                buttonNegativeAction?.invoke()
                            }
                        }
                    }
                }
                this.show(supportFragmentManager, System.currentTimeMillis().toString())
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun setTopToolbar(
        showMenu: Boolean = true,
        showSwitch: Boolean = true,
        title: Int? = null,
    ) {
        binding.topToolbar.showHide(showMenu)
        binding.swDelivery.showHide(showSwitch)
        if (title != null)
            binding.tvLabelDate.text = "${getString(title)}     ${Tools.getCurrentDate(true)} "
    }

    fun showLateralNavigation(show: Boolean = true) {
        binding.containerNavBar.showHide(show)
    }

}