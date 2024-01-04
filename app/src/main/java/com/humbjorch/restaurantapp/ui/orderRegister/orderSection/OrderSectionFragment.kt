package com.humbjorch.restaurantapp.ui.orderRegister.orderSection

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.R
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
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class OrderSectionFragment : Fragment() {

    private val viewModel: OrderSectionViewModel by viewModels()
    private lateinit var binding: FragmentOrderSectionBinding
    private lateinit var adapter: ProductsAdapter
    private lateinit var productTypeAdapter: ProductTypeAdapter
    private lateinit var productsOrderAdapter: ProductsOrderAdapter
    private lateinit var ingredientAdapter: IngredientsAdapter
    private var productSelected: ProductsOrderModel = ProductsOrderModel()
    private var productsOrder = listOf<ProductsOrderModel>()
    private var tablePosition = -1

    private val args: OrderSectionFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tablePosition = args.tablePosition
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
        Toast.makeText(requireContext(), "$tablePosition", Toast.LENGTH_SHORT).show()
        initAdapter()
        setListeners()
        setObserver()
    }

    private fun setObserver() {
        viewModel.liveDataRegisterOrder.observe(viewLifecycleOwner){
            when (it.status) {
                Status.LOADING -> {
                    (activity as MainActivity).showLoader()
                }

                Status.SUCCESS -> {
                    (activity as MainActivity).dismissLoader()
                    CustomToastWidget.show(
                        requireActivity(),
                        getString(R.string.message_exit),
                        TypeToast.SUCCESS
                    )
                    findNavController().navigate(R.id.action_orderSectionFragment_to_homeFragment)
                }

                Status.ERROR -> {
                    (activity as MainActivity).dismissLoader()
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListeners() {
        binding.btnAddProduct.setOnClickListener {
            newupdateOrder()
        }
        binding.btnIncreaseAmount.setOnClickListener {
            productSelected.amount = (productSelected.amount.toInt() + 1).toString()
            binding.tvLabelAmount.text = productSelected.amount
        }
        binding.btnMinusAmount.setOnClickListener {
            if (productSelected.amount.toInt() > 1) {
                productSelected.amount = (productSelected.amount.toInt() - 1).toString()
                binding.tvLabelAmount.text = productSelected.amount
            }
        }
        binding.btnDone.setOnClickListener {
            if (productsOrder.isNotEmpty()){
                val id = Tools.getCurrentDate()
                val order = OrderModel(
                    id = generateID(),
                    table = tablePosition.toString(),
                    productList = ArrayList(productsOrder),
                    time = getCurrentTime()
                )
                viewModel.saveOrder(id, order)
            }else{
                Toast.makeText(requireContext(), "todavia no tiene su orden lista", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentTime():String{
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return currentTime.format(formatter)
    }

    private fun initAdapter() {
        val productList = App.productListModel
        adapter = ProductsAdapter(productList) { product ->
            updateProductTypeList(product)
        }
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = adapter
        //---------------------------------------------------------------------------------
        val productTypeList = App.productListModel[0].list
        productTypeAdapter = ProductTypeAdapter(productTypeList) {
            updateIngredients(it)
        }
        binding.rvProductTypes.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvProductTypes.adapter = productTypeAdapter
        //---------------------------------------------------------------------------------
        val ingredientList = App.productListModel[0].list[0].ingredients!!
        ingredientAdapter = IngredientsAdapter(ingredientList) {
            updateIngredientsOrder()
        }
        binding.rvProductIngredients.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvProductIngredients.adapter = ingredientAdapter
        //---------------------------------------------------------------------------------
        productsOrderAdapter = ProductsOrderAdapter(productsOrder) { product, action ->
            actionListeners(product, action)
        }
        binding.rvProductsOrder.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductsOrder.adapter = productsOrderAdapter

    }

    private fun updateProductTypeList(product: ProductListModel) {
        //udpdate product order selected
        binding.tvProductPrice.text = product.list[0].price
        productSelected.product = product.list[0].name
        productSelected.price = product.list[0].price
        productSelected.ingredients = product.list[0].ingredients?.subList(0, 0) ?: emptyList()
        //update productType list
        productSelected.amount = "1"
        binding.tvLabelAmount.text = productSelected.amount.toString()
        adapter.updateList(App.productListModel)
        productTypeAdapter.clearSelectedPosition()
        productTypeAdapter.updateList(product.list)
    }

    private fun updateIngredients(product: ProductsModel) {
        //udpdate product order selected
        binding.tvProductPrice.text = product.price
        productSelected.product = product.name
        productSelected.price = product.price
        //update ingredients List
        productSelected.amount = "1"
        binding.tvLabelAmount.text = productSelected.amount
        ingredientAdapter.clearSelectedPositions()
        ingredientAdapter.updateList(product.ingredients!!)
        if (product.ingredients.isNotEmpty())
            productSelected.ingredients = listOf( product.ingredients[0])
    }

    private fun updateIngredientsOrder() {
        productSelected.ingredients = ingredientAdapter.getIngredients()
    }

    private fun newupdateOrder(){
        val newProductOrder = ProductsOrderModel(
            product = productSelected.product,
            amount = productSelected.amount,
            ingredients = productSelected.ingredients,
            price = productSelected.price
        )
        var updateProduct = false
        productsOrder.onEach {
            if (it.product == newProductOrder.product
                && it.ingredients == newProductOrder.ingredients){
                updateProduct = true
                it.amount += newProductOrder.amount
            }
        }
        if (!updateProduct){
            productsOrder = productsOrder.plus(newProductOrder)
            productsOrderAdapter.updateList(productsOrder)
        }
        else{
            productsOrderAdapter.updateList(productsOrder)
            productsOrderAdapter.notifyDataSetChanged()
        }
    }

    private fun actionListeners(product:ProductsOrderModel, action: ProductActionListener) = run {
        productsOrder = when (action) {
            ProductActionListener.ADD_PRODUCT -> {
                productsOrder.onEach {
                if (it.product == product.product && it.ingredients == product.ingredients)
                    it.amount = (it.amount.toInt() +1).toString()
                }
            }

            ProductActionListener.REMOVE_PRODUCT -> {
                productsOrder.onEach {
                    if (it.product == product.product && it.ingredients == product.ingredients)
                        (it.amount.toInt() -1).toString()
                }
            }
        }
        productsOrderAdapter.updateList(productsOrder)
        productsOrderAdapter.notifyDataSetChanged()
    }

}