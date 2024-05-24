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
import com.humbjorch.restaurantapp.data.model.OtherModel
import com.humbjorch.restaurantapp.data.model.ProductListModel
import com.humbjorch.restaurantapp.data.model.ProductsModel
import com.humbjorch.restaurantapp.data.model.ProductsOrderModel
import com.humbjorch.restaurantapp.databinding.DialogModalBottomSheetProductBinding
import com.humbjorch.restaurantapp.ui.orderRegister.RegisterOrderViewModel
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.NewOrderSectionViewModel
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.ExtraAdapter
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.IngredientsAdapter

class ModalBottomSheetProductDialog : BottomSheetDialogFragment() {

    private val activityViewModel: RegisterOrderViewModel by activityViewModels()
    private val viewModel: NewOrderSectionViewModel by viewModels()
    var products: ProductListModel = ProductListModel()

    private lateinit var binding : DialogModalBottomSheetProductBinding
    private lateinit var productsAdapter: ArrayAdapter<String>
    private lateinit var otherAdapter: ArrayAdapter<String>
    private lateinit var otherAdapter2: ArrayAdapter<String>
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
    }

    private fun setView() {
        binding.tvLabelIngredients.isVisible(ingredientList.isNotEmpty())
        binding.tvLabelExtras.isVisible(products.extras.isNotEmpty())
        binding.tvLabelIngredients.isVisible(ingredientList.isNotEmpty())
        binding.tvLabelExtras.isVisible(products.extras.isNotEmpty())

        if (products.other.isNotEmpty())
            binding.tiProductOther.hint = products.others[0].name
        if (products.others.size > 1)
            binding.tiProductOther1.hint = products.others[1].name
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
        val isVisibleOther1 = products.others.isNotEmpty()
        binding.tiProductOther.isVisible(isVisibleOther1)
        binding.tiProductOther1.isVisible(false)
        if (!isVisibleOther1) return

        val others1 = products.others[0]
        otherAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_dowm, others1.type)
        (binding.tiProductOther.editText as? AutoCompleteTextView)?.apply {
            setAdapter(otherAdapter)
            setText(others1.type[0], false)
        }
        setOnOtherSelected()
        //------------------------------------------------------------------------------------------
        val isVisibleOther2 = products.others.size > 1
        binding.tiProductOther1.isVisible(isVisibleOther2)

        if (!isVisibleOther2) return

        val others2 = products.others[1]
        otherAdapter2 = ArrayAdapter(requireContext(), R.layout.item_drop_dowm, others2.type)
        (binding.tiProductOther1.editText as? AutoCompleteTextView)?.apply {
            setAdapter(otherAdapter2)
            setText(others2.type[0], false)
        }
        setOnOtherSelected()
    }

    private fun setListeners() {
        binding.btnAddProduct.setOnClickListener {
            updateOrder()
        }
        (binding.tiProductType.editText as AutoCompleteTextView).setOnItemClickListener { _, _, position, _ ->
            val product = products.list[position]
            setOnProductSelected(product)
        }
        (binding.tiProductOther.editText as AutoCompleteTextView).setOnItemClickListener { _, _, _, _ ->
            setOnOtherSelected()
        }
        (binding.tiProductOther1.editText as AutoCompleteTextView).setOnItemClickListener { _, _, _, _ ->
            setOnOtherSelected()
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

    private fun setOnOtherSelected() {
        if (products.others.isEmpty())
            return

        val other = OtherModel(
            name = products.others[0].name,
            type = binding.tiProductOther.editText!!.text.toString(),
            )
        if (products.others.size > 1){
            val other2 = OtherModel(
                name = products.others[1].name,
                type = binding.tiProductOther1.editText!!.text.toString(),
            )
            activityViewModel.productSelection.value?.others = listOf(other,other2)
        }else{
            activityViewModel.productSelection.value?.others = listOf(other)
        }
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
        val productOrder = activityViewModel.productSelection.value!!
        val newProductOrder = ProductsOrderModel(
            product =productOrder.product,
            amount = productOrder.amount,
            ingredients = productOrder.ingredients,
            price = productOrder.price,
            extras = productOrder.extras,
            otherName = productOrder.otherName,
            other = productOrder.other,
            productType = products.productType,
            others = productOrder.others
        )

        var positionChanged = -1

        activityViewModel.productList.onEachIndexed { index, it ->
            if (it.product == newProductOrder.product &&
                it.ingredients == newProductOrder.ingredients &&
                it.extras == newProductOrder.extras &&
                it.others == newProductOrder.others
            ) {
                positionChanged = index
                it.amount = (it.amount.toInt() + newProductOrder.amount.toInt()).toString()
            }
        }
        if (positionChanged == -1) {
            activityViewModel.productList =  activityViewModel.productList.plus(newProductOrder)
        }
        extraAdapter.clearCheckBoxes()
        activityViewModel.productSelection.value!!.extras = emptyList()
        activityViewModel.productSelection.value!!.amount = "1"
        binding.tvLabelAmount.text = "1"
        binding.tvProductPrice.text = getString(R.string.label_price_product, productOrder.price.toInt())

        CustomToastWidget.show(
            requireActivity(),
            getString(R.string.label_add_product),
            TypeToast.SUCCESS
        )
    }

    companion object {
        const val TAG = "ModalBottomSheetDialog"
    }
}