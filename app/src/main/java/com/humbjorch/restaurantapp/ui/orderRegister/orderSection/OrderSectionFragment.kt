package com.humbjorch.restaurantapp.ui.orderRegister.orderSection

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.Constants.PRODUCT_DEFAULT_AMOUNT
import com.humbjorch.restaurantapp.core.utils.ProductActionListener
import com.humbjorch.restaurantapp.core.utils.Status
import com.humbjorch.restaurantapp.core.utils.Tools
import com.humbjorch.restaurantapp.core.utils.Tools.generateID
import com.humbjorch.restaurantapp.core.utils.alerts.CustomToastWidget
import com.humbjorch.restaurantapp.core.utils.alerts.TypeToast
import com.humbjorch.restaurantapp.data.model.OrderModel
import com.humbjorch.restaurantapp.data.model.ProductListModel
import com.humbjorch.restaurantapp.data.model.ProductsModel
import com.humbjorch.restaurantapp.data.model.ProductsOrderModel
import com.humbjorch.restaurantapp.databinding.FragmentOrderSectionBinding
import com.humbjorch.restaurantapp.ui.MainActivity
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.IngredientsAdapter
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.ProductTypeAdapter
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.ProductsAdapter
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.ProductsOrderAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OrderSectionFragment : Fragment() {

    private val viewModel: OrderSectionViewModel by viewModels()
    private lateinit var binding: FragmentOrderSectionBinding
    private lateinit var productAdapter: ProductsAdapter
    private lateinit var productTypeAdapter: ProductTypeAdapter
    private lateinit var productsOrderAdapter: ProductsOrderAdapter
    private lateinit var ingredientAdapter: IngredientsAdapter
    private var productsOrder = listOf<ProductsOrderModel>()
    private var tablePosition = -1
    private val args: OrderSectionFragmentArgs by navArgs()
    private lateinit var orderModel: OrderModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tablePosition = args.tablePosition
        orderModel = args.order ?: OrderModel()
        productsOrder = orderModel.productList
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderSectionBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setListeners()
        setObserver()
        setView()
    }

    private fun setView() {
        if (orderModel.productList.isNotEmpty())
            binding.btnDone.text = getString(R.string.label_save_button)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setObserver() {
        viewModel.liveDataRegisterOrder.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    (activity as MainActivity).showLoader()
                }

                Status.SUCCESS -> {
                    viewModel.printOrder(orderModel)
                    CustomToastWidget.show(
                        requireActivity(),
                        getString(R.string.message_exit),
                        TypeToast.SUCCESS
                    )
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
        viewModel.liveDataPrint.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    (activity as MainActivity).dismissLoader()
                    findNavController().navigate(R.id.action_orderSectionFragment_to_homeFragment)
                }

                Status.ERROR -> {
                    (activity as MainActivity).dismissLoader()
                    CustomToastWidget.show(
                        activity = requireActivity(),
                        message = it.message,
                        type = TypeToast.ERROR
                    )
                }
                else -> { }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListeners() {
        binding.btnAddProduct.setOnClickListener {
            updateOrder()
        }
        binding.btnIncreaseAmount.setOnClickListener {
            viewModel.productSelected.amount =
                (viewModel.productSelected.amount.toInt() + 1).toString()
            binding.tvLabelAmount.text = viewModel.productSelected.amount
        }
        binding.btnMinusAmount.setOnClickListener {
            if (viewModel.productSelected.amount.toInt() > 1) {
                viewModel.productSelected.amount =
                    (viewModel.productSelected.amount.toInt() - 1).toString()
                binding.tvLabelAmount.text = viewModel.productSelected.amount
            }
        }
        binding.btnDone.setOnClickListener {
            if (productsOrder.isNotEmpty()) {
                (activity as MainActivity).genericAlert(
                    titleAlert = getString(R.string.dialog_title_confirmation_order),
                    descriptionAlert = getString(R.string.dialog_description_confirmation_order),
                    txtBtnNegativeAlert = getString(R.string.dialog_cancel_button),
                    txtBtnPositiveAlert = getString(R.string.dialog_positive_button),
                    buttonPositiveAction = {
                        orderModel = OrderModel(
                            id = if (orderModel.id != "") orderModel.id else generateID(),
                            table = tablePosition.toString(),
                            productList = ArrayList(productsOrder),
                            time = Tools.getCurrentTime()
                        )
                        viewModel.saveOrder(orderModel)
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
    }

    private fun initAdapter() {
        val productList = App.productListModel
        productAdapter = ProductsAdapter(productList) { product ->
            updateProductTypeList(product)
        }
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        binding.rvProducts.adapter = productAdapter
        //---------------------------------------------------------------------------------
        val productTypeList = App.productListModel[0].list
        productTypeAdapter = ProductTypeAdapter(productTypeList) {
            updateIngredients(it)
        }
        binding.rvProductTypes.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rvProductTypes.adapter = productTypeAdapter
        //---------------------------------------------------------------------------------
        val ingredientList = App.productListModel[0].list[0].ingredients!!
        ingredientAdapter = IngredientsAdapter(ingredientList) {
            updateIngredientsSelected()
        }
        binding.rvProductIngredients.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rvProductIngredients.adapter = ingredientAdapter
        //---------------------------------------------------------------------------------
        productsOrderAdapter = ProductsOrderAdapter(productsOrder) { product, action ->
            actionListenersOrders(product, action)
        }
        binding.rvProductsOrder.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductsOrder.adapter = productsOrderAdapter
    }

    private fun updateProductTypeList(product: ProductListModel) {
        //update product order selected
        binding.tvProductPrice.text = getString(R.string.label_price_product, product.list[0].price.toInt())
        viewModel.productSelected.product = product.list[0].name
        viewModel.productSelected.price = product.list[0].price
        viewModel.productSelected.ingredients =
            product.list[0].ingredients?.subList(0, 0) ?: emptyList()
        //update productType list
        viewModel.productSelected.amount = PRODUCT_DEFAULT_AMOUNT
        binding.tvLabelAmount.text = PRODUCT_DEFAULT_AMOUNT
        productTypeAdapter.clearSelectedPosition()
        productTypeAdapter.updateList(product.list)
    }

    private fun updateIngredients(product: ProductsModel) {
        //update product order selected
        binding.tvProductPrice.text = getString(R.string.label_price_product, product.price.toInt())
        viewModel.productSelected.product = product.name
        viewModel.productSelected.price = product.price
        //update ingredients List
        viewModel.productSelected.amount = PRODUCT_DEFAULT_AMOUNT
        binding.tvLabelAmount.text = PRODUCT_DEFAULT_AMOUNT
        ingredientAdapter.updateList(product.ingredients!!)
        if (product.ingredients.isNotEmpty())
            viewModel.productSelected.ingredients = listOf(product.ingredients[0])
        ingredientAdapter.clearCheckBoxes()
    }

    private fun updateIngredientsSelected() {
        viewModel.productSelected.ingredients = ingredientAdapter.getIngredients()
    }

    private fun updateOrder() {
        val newProductOrder = ProductsOrderModel(
            product = viewModel.productSelected.product,
            amount = viewModel.productSelected.amount,
            ingredients = viewModel.productSelected.ingredients,
            price = viewModel.productSelected.price
        )
        var positionChanged = -1
        productsOrder.onEachIndexed { index, it ->
            if (it.product == newProductOrder.product
                && it.ingredients == newProductOrder.ingredients
            ) {
                positionChanged = index
                it.amount = (it.amount.toInt() + newProductOrder.amount.toInt()).toString()
            }
        }
        if (positionChanged == -1) {
            productsOrder = productsOrder.plus(newProductOrder)
            productsOrderAdapter.updateList(productsOrder)
            productsOrderAdapter.notifyItemInserted(productsOrder.size - 1)
        } else {
            productsOrderAdapter.updateList(productsOrder)
            productsOrderAdapter.notifyItemChanged(positionChanged)
        }
        viewModel.productSelected.amount = "1"
        binding.tvLabelAmount.text = "1"
    }

    private fun actionListenersOrders(product: ProductsOrderModel, action: ProductActionListener) = run {
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
        if (itemChanged.amount.toInt() >= 1){
            productsOrderAdapter.updateList(productsOrder)
            productsOrderAdapter.notifyItemChanged(position)
        }
        else{
            productsOrder = productsOrder.minus(itemChanged)
            productsOrderAdapter.updateList(productsOrder)
        }
    }

}