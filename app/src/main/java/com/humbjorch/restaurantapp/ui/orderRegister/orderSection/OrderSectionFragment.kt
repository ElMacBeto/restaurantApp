package com.humbjorch.restaurantapp.ui.orderRegister.orderSection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.data.model.HamburgerModel
import com.humbjorch.restaurantapp.data.model.ProductsOrderModel
import com.humbjorch.restaurantapp.databinding.FragmentOrderSectionBinding
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.ProductTypeAdapter
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.ProductsAdapter
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.ProductsOrderAdapter


class OrderSectionFragment : Fragment() {

    private lateinit var binding: FragmentOrderSectionBinding
    private lateinit var adapter: ProductsAdapter
    private lateinit var productTypeAdapter:ProductTypeAdapter
    private lateinit var productsOrderAdapter: ProductsOrderAdapter
    private var productSelected: ProductsOrderModel? = null
    private var productsOrder = emptyList<ProductsOrderModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderSectionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        setListeners()
    }

    private fun setListeners() {
        binding.btnAddProduct.setOnClickListener {
        if (productSelected != null)
            updateOrder(productSelected!!)
        }
    }

    private fun initAdapter() {
        val productList = App.hamburgers
        adapter = ProductsAdapter(productList) {product, ingredient ->
            updateProductTypeList(product, ingredient)
        }
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = adapter
        //---------------------------------------------------------------------------------
        productsOrderAdapter = ProductsOrderAdapter(productsOrder){

        }
        binding.rvProductsOrder.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductsOrder.adapter = productsOrderAdapter
        //---------------------------------------------------------------------------------
        val productTypeList = App.hamburgers[0].ingredients
        binding.tvProductDescription.text =  App.hamburgers[0].description
        productTypeAdapter = ProductTypeAdapter(productTypeList) {

        }
        binding.rvProductTypes.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvProductTypes.adapter = productTypeAdapter
    }

    private fun updateProductTypeList(product: HamburgerModel, ingredient: String) {
        productSelected = ProductsOrderModel(
            product = product.name,
            ingredient = ingredient,
            price = product.price.toInt()
        )
        App.hamburgers.onEach {
            it.isClicked = product.name == it.name
        }
        adapter.updateList(App.hamburgers)
        binding.tvProductDescription.text = product.description
        productTypeAdapter.updateList(product.ingredients)
    }

    private fun updateOrder(productOrder: ProductsOrderModel){
        productsOrder = productsOrder.plus(productOrder)
        productsOrderAdapter.updateList(productsOrder)
    }

}