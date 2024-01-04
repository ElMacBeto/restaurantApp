package com.humbjorch.restaurantapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.humbjorch.restaurantapp.ui.MainActivity
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.core.utils.alerts.CustomToastWidget
import com.humbjorch.restaurantapp.core.utils.alerts.TypeToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        viewModel.setAllProducts()
    }

    private fun setObservers() {
        viewModel.setProductsLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    (activity as MainActivity).showLoader()
                }

                Status.SUCCESS -> {
                    (activity as MainActivity).dismissLoader()
                    viewModel.setProducts(it.data!!)
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                    }, 1500)
                }

                Status.ERROR -> {
                    (activity as MainActivity).dismissLoader()
                    CustomToastWidget.show(
                        activity = requireActivity(),
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                }
            }
        }
    }

}