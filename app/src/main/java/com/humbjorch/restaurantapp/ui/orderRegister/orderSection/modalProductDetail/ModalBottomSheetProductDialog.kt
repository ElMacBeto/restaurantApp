package com.humbjorch.restaurantapp.ui.orderRegister.orderSection.modalProductDetail

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.Constants
import com.humbjorch.restaurantapp.core.utils.alerts.CustomToastWidget
import com.humbjorch.restaurantapp.core.utils.alerts.TypeToast
import com.humbjorch.restaurantapp.core.utils.isVisible
import com.humbjorch.restaurantapp.data.model.ProductListModel
import com.humbjorch.restaurantapp.data.model.ProductsModel
import com.humbjorch.restaurantapp.data.model.ProductsOrderModel
import com.humbjorch.restaurantapp.databinding.DialogModalBottomSheetProductBinding
import com.humbjorch.restaurantapp.ui.orderRegister.RegisterOrderViewModel
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.NewOrderSectionViewModel
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.ExtraAdapter
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.IngredientsAdapter

class ModalBottomSheetProductDialog() : BottomSheetDialogFragment() {

    private val activityViewModel: RegisterOrderViewModel by activityViewModels()
    private val viewModel: NewOrderSectionViewModel by viewModels()
    var products: ProductListModel = ProductListModel()

    private lateinit var binding : DialogModalBottomSheetProductBinding
    private lateinit var productsAdapter: ArrayAdapter<String>
    private lateinit var otherAdapter: ArrayAdapter<String>
    private lateinit var ingredientAdapter: IngredientsAdapter
    private lateinit var extraAdapter: ExtraAdapter

    private var ingredientList: List<String> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogModalBottomSheetProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapters()
        initProductDropDownMenu()
        initOtherDropDownMenu()
        setListeners()
        setView()
        activityViewModel.productSelection.value?.otherName = products.other
    }

    private fun setView() {
        binding.tvLabelIngredients.isVisible(ingredientList.isNotEmpty())
        binding.tvLabelExtras.isVisible(products.extras.isNotEmpty())
        binding.tvLabelIngredients.isVisible(ingredientList.isNotEmpty())
        binding.tvLabelExtras.isVisible(products.extras.isNotEmpty())
        binding.tiProductOther.isVisible(products.other.isNotEmpty())
    }

    private fun initProductDropDownMenu() {
        val items: List<String> = products.list.map { it.name }
        productsAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_dowm, items)
        (binding.tiProductType.editText as? AutoCompleteTextView)?.apply {
            setAdapter(productsAdapter)
            setText(items[0], false)
        }
        setOnProductSelected(products.list[0])
    }

    private fun initOtherDropDownMenu(){
        val others = products.otherList
        if (others.isEmpty()){
            binding.tiProductOther.visibility = View.GONE
            return
        }
        otherAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_dowm, others)
        (binding.tiProductOther.editText as? AutoCompleteTextView)?.apply {
            setAdapter(otherAdapter)
            setText(others[0], false)
        }
        setOnOtherSelected(products.otherList[0])
    }

    private fun setListeners() {
        binding.btnAddProduct.setOnClickListener {
            updateOrder()
            CustomToastWidget.show(
                requireActivity(),
                getString(R.string.label_add_product),
                TypeToast.SUCCESS
            )
        }
        (binding.tiProductType.editText as AutoCompleteTextView).setOnItemClickListener { _, _, position, _ ->
            val product = products.list[position]
            setOnProductSelected(product)
        }
        (binding.tiProductOther.editText as AutoCompleteTextView).setOnItemClickListener { _, _, position, _ ->
            val other = products.otherList[position]
            setOnOtherSelected(other)
        }
        binding.btnIncreaseAmount.setOnClickListener {
            activityViewModel.productSelection.value?.amount =
                (activityViewModel.productSelection.value?.amount!!.toInt() + 1).toString()

            binding.tvLabelAmount.text = activityViewModel.productSelection.value?.amount
        }
        binding.btnMinusAmount.setOnClickListener {
            if ( activityViewModel.productSelection.value?.amount!!.toInt() > 1) {
                activityViewModel.productSelection.value?.amount =
                    (activityViewModel.productSelection.value!!.amount.toInt() - 1).toString()
                binding.tvLabelAmount.text = activityViewModel.productSelection.value?.amount
            }
        }
    }

    private fun initAdapters() {
        val spanColumn = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 3
        ingredientList = products.list[0].ingredients!!
        ingredientAdapter = IngredientsAdapter(ingredientList) {
            updateIngredientsSelected()
        }
        binding.rvProductIngredients.layoutManager = GridLayoutManager(requireContext(), spanColumn)
        binding.rvProductIngredients.adapter = ingredientAdapter
        //-----------------------------------------------------------------------------
        val extras = products.extras
        extraAdapter = ExtraAdapter(extras) {
            updateExtraSelected()
        }
        binding.rvProductExtras.layoutManager = GridLayoutManager(requireContext(), spanColumn)
        binding.rvProductExtras.adapter = extraAdapter
    }

    private fun setOnProductSelected(product: ProductsModel) {
        //update product order selected
        binding.tvProductPrice.text = getString(R.string.label_price_product, product.price.toInt())
        activityViewModel.productSelection.value?.product = product.name
        activityViewModel.productSelection.value?.price = product.price
        //set default amount value of product
        activityViewModel.productSelection.value?.amount = Constants.PRODUCT_DEFAULT_AMOUNT
        binding.tvLabelAmount.text = Constants.PRODUCT_DEFAULT_AMOUNT
        //update ingredients
        ingredientList = product.ingredients!!
        ingredientAdapter.updateList(ingredientList)
        ingredientAdapter.clearCheckBoxes()
        activityViewModel.productSelection.value?.ingredients = ingredientAdapter.getIngredients()
        extraAdapter.clearCheckBoxes()
        setView()
    }

    private fun setOnOtherSelected(other: String) {
        activityViewModel.productSelection.value?.other = other
    }

    private fun updateIngredientsSelected() {
        activityViewModel.productSelection.value?.ingredients = ingredientAdapter.getIngredients()
    }

    private fun updateExtraSelected() {
        val newExtras = extraAdapter.getExtras()
        val productPrice= activityViewModel.productSelection.value?.price ?: ""
        activityViewModel.productSelection.value?.extras = newExtras
        val newProductPrice = viewModel.getProductPrice(productPrice, newExtras)
        binding.tvProductPrice.text = getString(R.string.label_price_product, newProductPrice)
    }

    private fun updateOrder() {
        val newProductOrder = activityViewModel.productSelection.value!!
        val productList =  activityViewModel.productList.value
        newProductOrder.productType = products.productType
        viewModel.updateOrder(newProductOrder, productList){
            activityViewModel.productList.value = it
        }
        extraAdapter.clearCheckBoxes()
        activityViewModel.productSelection.value = ProductsOrderModel()
        binding.tvLabelAmount.text = "1"
    }

    companion object {
        const val TAG = "ModalBottomSheetDialog"
    }
}