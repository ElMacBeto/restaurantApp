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
import com.humbjorch.restaurantapp.core.utils.extDismissLoader
import com.humbjorch.restaurantapp.core.utils.extShowLoader
import com.humbjorch.restaurantapp.core.utils.genericAlert
import com.humbjorch.restaurantapp.core.utils.isVisible
import com.humbjorch.restaurantapp.databinding.ActivityMainBinding
import com.humbjorch.restaurantapp.ui.history.HistoryFragmentDirections
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
    private var historyNav: (() -> Unit)? = null

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
                    homeNav = null
                    settingNav = {
                        val action = HomeFragmentDirections.actionHomeFragmentToSettingsFragment()
                        navController.navigate(action)
                    }
                    summaryNav = {
                        val action = HomeFragmentDirections.actionHomeFragmentToSummaryFragment()
                        navController.navigate(action)
                    }
                    historyNav = {
                        val action = HomeFragmentDirections.actionHomeFragmentToHistoryFragment()
                        navController.navigate(action)
                    }
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
                    historyNav = {
                        val action = SettingsFragmentDirections.actionSettingsFragmentToHistoryFragment()
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
                    historyNav = {
                        val action = SummaryFragmentDirections.actionSummaryFragmentToHistoryFragment()
                        navController.navigate(action)
                    }
                }

                R.id.historyFragment -> {
                    setTopToolbar(
                        showMenu = true,
                        title = R.string.label_title_history,
                        showSwitch = false
                    )
                    showLateralNavigation(true)
                    settingNav = {
                        val action = HistoryFragmentDirections.actionHistoryFragmentToSettingsFragment()
                        navController.navigate(action)
                    }
                    homeNav = {
                        val action = HistoryFragmentDirections.actionHistoryFragmentToHomeFragment()
                        navController.navigate(action)
                    }
                    summaryNav = {
                        val action = HistoryFragmentDirections.actionHistoryFragmentToSummaryFragment()
                        navController.navigate(action)
                    }
                    historyNav = null
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
            binding.navBar.btnHistory.backgroundTintList = colorStateList
        }
        binding.navBar.btnHome.setOnClickListener {
            homeNav?.invoke()

            val colorFromResource = ContextCompat.getColor(this, R.color.blue2)
            val colorSelected = ColorStateList.valueOf(colorFromResource)
            binding.navBar.btnHome.backgroundTintList = colorSelected

            val colorStateList = ColorStateList.valueOf(Color.WHITE)
            binding.navBar.btnSettings.backgroundTintList = colorStateList
            binding.navBar.btnInventory.backgroundTintList = colorStateList
            binding.navBar.btnHistory.backgroundTintList = colorStateList
        }
        binding.navBar.btnInventory.setOnClickListener {
            summaryNav?.invoke()

            val colorFromResource = ContextCompat.getColor(this, R.color.blue2)
            val colorSelected = ColorStateList.valueOf(colorFromResource)
            binding.navBar.btnInventory.backgroundTintList = colorSelected

            val colorStateList = ColorStateList.valueOf(Color.WHITE)
            binding.navBar.btnHome.backgroundTintList = colorStateList
            binding.navBar.btnSettings.backgroundTintList = colorStateList
            binding.navBar.btnHistory.backgroundTintList = colorStateList
        }
        binding.navBar.btnHistory.setOnClickListener {
            historyNav?.invoke()

            val colorFromResource = ContextCompat.getColor(this, R.color.blue2)
            val colorSelected = ColorStateList.valueOf(colorFromResource)
            binding.navBar.btnHistory.backgroundTintList = colorSelected

            val colorStateList = ColorStateList.valueOf(Color.WHITE)
            binding.navBar.btnHome.backgroundTintList = colorStateList
            binding.navBar.btnSettings.backgroundTintList = colorStateList
            binding.navBar.btnInventory.backgroundTintList = colorStateList
        }
    }

    fun showLoader() {
        extShowLoader(loader)
    }

    fun dismissLoader() {
        extDismissLoader(loader)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun setTopToolbar(
        showMenu: Boolean = true,
        showSwitch: Boolean = true,
        title: Int? = null,
    ) {
        binding.topToolbar.isVisible(showMenu)
        binding.swDelivery.isVisible(showSwitch)
        binding.btnRefresh.isVisible(showSwitch)
        if (title != null)
            binding.tvLabelDate.text = "${getString(title)}     ${Tools.getCurrentDate(true)} "
    }

    fun showLateralNavigation(show: Boolean = true) {
        binding.containerNavBar.isVisible(show)
    }

}