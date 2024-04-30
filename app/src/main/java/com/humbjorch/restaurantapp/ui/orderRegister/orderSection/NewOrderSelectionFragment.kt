package com.humbjorch.restaurantapp.ui.orderRegister.orderSection

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.ProductActionListener
import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.core.utils.alerts.CustomToastWidget
import com.humbjorch.restaurantapp.core.utils.alerts.TypeToast
import com.humbjorch.restaurantapp.core.utils.genericAlert
import com.humbjorch.restaurantapp.data.model.ProductsOrderModel
import com.humbjorch.restaurantapp.databinding.FragmentNewOrderSelectionBinding
import com.humbjorch.restaurantapp.ui.orderRegister.OrderRegisterActivity
import com.humbjorch.restaurantapp.ui.orderRegister.RegisterOrderViewModel
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.ProductsAdapter
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.ProductsOrderAdapter
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.modalProductDetail.ModalBottomSheetProductDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewOrderSelectionFragment : Fragment() {

    private val activityViewModel: RegisterOrderViewModel by activityViewModels()
    private lateinit var binding: FragmentNewOrderSelectionBinding
    private lateinit var productAdapter: ProductsAdapter
    private lateinit var productsOrderAdapter: ProductsOrderAdapter
    private val modal by lazy { ModalBottomSheetProductDialog() }

    private var productsOrder = listOf<ProductsOrderModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewOrderSelectionBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setListeners()
        setInitView()
        setObservers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setObservers() {
        activityViewModel.liveDataRegisterOrder.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    (activity as OrderRegisterActivity).showLoader()
                }

                Status.SUCCESS -> {
                    if (!activityViewModel.isEditOrder) {
                        activityViewModel.printOrder()
                    } else {
                        (activity as OrderRegisterActivity).dismissLoader()
                        CustomToastWidget.show(
                            requireActivity(),
                            getString(R.string.message_exit),
                            TypeToast.SUCCESS
                        )
                        requireActivity().finish()
                    }
                }

                Status.ERROR -> {
                    (activity as OrderRegisterActivity).dismissLoader()
                    CustomToastWidget.show(
                        activity = requireActivity(),
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                }
            }
        }
        activityViewModel.liveDataPrint.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    (activity as OrderRegisterActivity).dismissLoader()
                    requireActivity().finish()
                }

                Status.ERROR -> {
                    (activity as OrderRegisterActivity).dismissLoader()
                    CustomToastWidget.show(
                        activity = requireActivity(),
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                    requireActivity().finish()
                }
                else -> { }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListeners() {
        binding.imgCar.setOnClickListener {
            binding.lyDrawer.openDrawer(GravityCompat.END)
            productsOrder = activityViewModel.productList.value ?: emptyList()
            productsOrderAdapter = ProductsOrderAdapter(productsOrder) { product, action ->
                actionListenersOrders(product, action)
            }
            binding.rvProductsOrder.layoutManager = LinearLayoutManager(requireContext())
            binding.rvProductsOrder.adapter = productsOrderAdapter

        }
        binding.btnDone.setOnClickListener {
            onDoneOrderListener()
        }
    }

    private fun initAdapter() {
        val productList = App.productListModel
        productAdapter = ProductsAdapter(productList) { products ->
            modal.products = products
            modal.show(parentFragmentManager, ModalBottomSheetProductDialog.TAG)
        }
        val spanColumn =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 5 else 3
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), spanColumn)
        binding.rvProducts.adapter = productAdapter
        //order products------------------------------------------------------------------------------
        productsOrder = activityViewModel.productList.value ?: emptyList()
        productsOrderAdapter = ProductsOrderAdapter(productsOrder) { product, action ->
            actionListenersOrders(product, action)
        }
        binding.rvProductsOrder.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductsOrder.adapter = productsOrderAdapter
    }

    private fun setInitView() {
        if (activityViewModel.order == null)
            return

        binding.btnDone.text = getString(R.string.label_save_button)
        productsOrder = activityViewModel.order!!.productList
        productsOrderAdapter.updateList(productsOrder)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onDoneOrderListener() {
        if (activityViewModel.productList.value!!.isNotEmpty()) {
            (activity as OrderRegisterActivity).genericAlert(
                titleAlert = getString(R.string.dialog_title_confirmation_order),
                descriptionAlert = getString(R.string.dialog_description_confirmation_order),
                txtBtnNegativeAlert = getString(R.string.dialog_cancel_button),
                txtBtnPositiveAlert = getString(R.string.dialog_positive_button),
                buttonPositiveAction = {
                    activityViewModel.saveOrder()
                    binding.lyDrawer.closeDrawer(GravityCompat.END)
                },
                buttonNegativeAction = { }
            )
        } else {
            CustomToastWidget.show(
                requireActivity(),
                getString(R.string.label_empty_order),
                TypeToast.WARNING
            )
        }
    }

    private fun actionListenersOrders(product: ProductsOrderModel, action: ProductActionListener) =
        run {
            var position = 0
            productsOrder = when (action) {
                ProductActionListener.ADD_PRODUCT -> {
                    productsOrder.onEachIndexed { index, it ->
                        if (it.product == product.product && it.ingredients == product.ingredients) {
                            position = index
                            it.amount = (it.amount.toInt() + 1).toString()
                        }
                    }
                }

                ProductActionListener.REMOVE_PRODUCT -> {
                    productsOrder.onEachIndexed { index, it ->
                        if (it.product == product.product && it.ingredients == product.ingredients) {
                            position = index
                            it.amount = (it.amount.toInt() - 1).toString()
                        }
                    }
                }
            }

            val itemChanged = productsOrder[position]
            if (itemChanged.amount.toInt() >= 1) {
                productsOrderAdapter.updateList(productsOrder)
                productsOrderAdapter.notifyItemChanged(position)
            } else {
                productsOrder = productsOrder.minus(itemChanged)
                productsOrderAdapter.updateList(productsOrder)
            }
        }

}