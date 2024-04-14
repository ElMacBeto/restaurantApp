package com.humbjorch.restaurantapp.ui.orderRegister.orderSection.modalProductDetail

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.core.utils.Constants
import com.humbjorch.restaurantapp.core.utils.showHide
import com.humbjorch.restaurantapp.data.model.ProductListModel
import com.humbjorch.restaurantapp.data.model.ProductsModel
import com.humbjorch.restaurantapp.data.model.ProductsOrderModel
import com.humbjorch.restaurantapp.databinding.DialogModalBottomSheetProductBinding
import com.humbjorch.restaurantapp.ui.orderRegister.RegisterOrderViewModel
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.ExtraAdapter
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.IngredientsAdapter

class ModalBottomSheetProductDialog(private val products: ProductListModel) : BottomSheetDialogFragment() {

    private val activityViewModel: RegisterOrderViewModel by activityViewModels()
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
        initDropDownMenus()
        setListeners()
    }

    private fun initDropDownMenus() {
        val items: List<String> = products.list.map { it.name }
        productsAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_dowm, items)
        (binding.tiProductType.editText as? AutoCompleteTextView)?.setAdapter(productsAdapter)

        val others = products.otherList
        otherAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_dowm, others)
        (binding.tiProductOther.editText as? AutoCompleteTextView)?.setAdapter(otherAdapter)
    }

    private fun setListeners() {
        binding.btnAddProduct.setOnClickListener {
            updateOrder()
            this@ModalBottomSheetProductDialog.dismiss()
        }
        (binding.tiProductType.editText as AutoCompleteTextView).setOnItemClickListener { _, _, position, _ ->
            val product = products.list[position]
            setOnProductSelected(product)
        }
        (binding.tiProductOther.editText as AutoCompleteTextView).setOnItemClickListener { _, _, position, _ ->
            val other = products.otherList[position]
            setOnOtherSelected(other)
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
        hideOrShowIngredientsAndExtras()
    }

    private fun setOnOtherSelected(other: String) {
        activityViewModel.productSelection.value?.other = other
    }

    private fun updateIngredientsSelected() {
        activityViewModel.productSelection.value?.ingredients = ingredientAdapter.getIngredients()
    }

    private fun updateExtraSelected() {
        activityViewModel.productSelection.value?.extras = extraAdapter.getExtras()
    }

    private fun hideOrShowIngredientsAndExtras(){
        binding.tvLabelIngredients.showHide(ingredientList.isNotEmpty())
        binding.tvLabelExtras.showHide(products.extras.isNotEmpty())
        binding.tiProductOther.showHide(products.other.isNotEmpty())
    }

    private fun updateOrder() {
        if ( activityViewModel.productSelection.value?.product.isNullOrEmpty())
            return
        val newProductOrder = ProductsOrderModel(
            product = activityViewModel.productSelection.value?.product!!,
            amount = activityViewModel.productSelection.value?.amount!!,
            ingredients = activityViewModel.productSelection.value?.ingredients!!,
            price = activityViewModel.productSelection.value?.price!!,
            extras = activityViewModel.productSelection.value?.extras!!,
            otherName = activityViewModel.productSelection.value?.otherName!!,
            other = activityViewModel.productSelection.value?.other!!,
            productType = products.productType
        )

        var positionChanged = -1

        activityViewModel.productList.value!!.onEachIndexed { index, it ->
            if (it.product == newProductOrder.product &&
                it.ingredients == newProductOrder.ingredients &&
                it.extras == newProductOrder.extras &&
                it.other == newProductOrder.other
            ) {
                positionChanged = index
                it.amount = (it.amount.toInt() + newProductOrder.amount.toInt()).toString()
            }
        }
        if (positionChanged == -1) {
            activityViewModel.productList.value = activityViewModel.productList.value!!.plus(newProductOrder)
        }

        extraAdapter.clearCheckBoxes()
        activityViewModel.productSelection.value = ProductsOrderModel()
        binding.tvLabelAmount.text = "1"
    }

    companion object {
        const val TAG = "ModalBottomSheetDialog"
    }
}