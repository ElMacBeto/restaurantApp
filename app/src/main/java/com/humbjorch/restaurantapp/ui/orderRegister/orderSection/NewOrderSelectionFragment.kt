package com.humbjorch.restaurantapp.ui.orderRegister.orderSection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.humbjorch.restaurantapp.App
import com.humbjorch.restaurantapp.databinding.FragmentNewOrderSelectionBinding
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.ProductsAdapter
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.modalProductDetail.ModalBottomSheetProductDialog


class NewOrderSelectionFragment : Fragment() {

    private lateinit var binding: FragmentNewOrderSelectionBinding
    private lateinit var productAdapter: ProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewOrderSelectionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    private fun initAdapter() {
        val productList = App.productListModel
        productAdapter = ProductsAdapter(productList) { products ->
            val modal = ModalBottomSheetProductDialog(products)
            modal.show(parentFragmentManager, ModalBottomSheetProductDialog.TAG)
        }
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 5,)
        binding.rvProducts.adapter = productAdapter
    }


}