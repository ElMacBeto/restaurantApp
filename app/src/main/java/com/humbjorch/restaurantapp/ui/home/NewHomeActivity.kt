package com.humbjorch.restaurantapp.ui.home

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.LoaderNBEXWidget
import com.humbjorch.restaurantapp.core.utils.extDismissLoader
import com.humbjorch.restaurantapp.core.utils.extShowLoader
import com.humbjorch.restaurantapp.core.utils.genericAlert
import com.humbjorch.restaurantapp.databinding.ActivityNewHomeBinding
import com.humbjorch.restaurantapp.ui.history.HistoryFragmentDirections
import com.humbjorch.restaurantapp.ui.settings.SettingsFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewHomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewHomeBinding
    private val loader by lazy { LoaderNBEXWidget() }
    private lateinit var navController: NavController
    private var settingNav: (() -> Unit)? = null
    private var homeNav: (() -> Unit)? = null
    private var historyNav: (() -> Unit)? = null

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            genericAlert(
                titleAlert = getString(R.string.dialog_title_confirmation),
                descriptionAlert = getString(R.string.dialog_description_finish_app),
                txtBtnNegativeAlert = getString(R.string.dialog_cancel_button),
                txtBtnPositiveAlert = getString(R.string.dialog_positive_button),
                buttonPositiveAction = { finish() },
                buttonNegativeAction = { }
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.fragmentContainer.id) as NavHostFragment
        navController = navHostFragment.navController
        onBackPressedDispatcher.addCallback(this, callback)
        setNavigationChange()
        setListeners()
    }

    private fun setListeners() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    homeNav?.invoke()
                    true
                }

                R.id.navigation_history -> {
                    historyNav?.invoke()
                    true
                }

                R.id.navigation_settings -> {
                    settingNav?.invoke()
                    true
                }

                else -> false
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setNavigationChange() {
        navController.addOnDestinationChangedListener { _, fragment, _ ->
            when (fragment.id) {
                R.id.newHomeFragment -> {
                    homeNav = null
                    settingNav = {
                        val action =
                            NewHomeFragmentDirections.actionNewHomeFragmentToSettingsFragment2()
                        navController.navigate(action)
                    }
                    historyNav = {
                        val action =
                            NewHomeFragmentDirections.actionNewHomeFragmentToHistoryFragment2()
                        navController.navigate(action)
                    }
                }

                R.id.settingsFragment2 -> {
                    settingNav = null
                    homeNav = {
                        val action =
                            SettingsFragmentDirections.actionSettingsFragment2ToNewHomeFragment()
                        navController.navigate(action)
                    }
                    historyNav = {
                        val action =
                            SettingsFragmentDirections.actionSettingsFragment2ToHistoryFragment2()
                        navController.navigate(action)
                    }
                }

                R.id.historyFragment2 -> {
                    historyNav = null
                    settingNav = {
                        val action =
                            HistoryFragmentDirections.actionHistoryFragment2ToSettingsFragment2()
                        navController.navigate(action)
                    }
                    homeNav = {
                        val action =
                            HistoryFragmentDirections.actionHistoryFragment2ToNewHomeFragment()
                        navController.navigate(action)
                    }
                }
            }
        }
    }

    fun showLoader() {
        extShowLoader(loader)
    }

    fun dismissLoader() {
        extDismissLoader(loader)
    }


}