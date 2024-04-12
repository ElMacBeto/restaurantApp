package com.humbjorch.restaurantapp.ui.orderRegister.orderSection.modalProductDetail

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.data.model.ProductListModel
import com.humbjorch.restaurantapp.databinding.DialogModalBottomSheetProductBinding
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.ExtraAdapter
import com.humbjorch.restaurantapp.ui.orderRegister.orderSection.adapter.IngredientsAdapter

class ModalBottomSheetProductDialog(private val products: ProductListModel) : BottomSheetDialogFragment() {

    private lateinit var binding : DialogModalBottomSheetProductBinding
    private lateinit var ingredientAdapter: IngredientsAdapter
    private lateinit var extraAdapter: ExtraAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogModalBottomSheetProductBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onStart() {
        super.onStart()
        //this forces the sheet to appear at max height even on landscape
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.setOnShowListener { it ->
            val d = it as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()
        setListeners()
    }

    private fun setListeners() {
        binding.btnAddProduct.setOnClickListener {
            this@ModalBottomSheetProductDialog.dismiss()
        }
    }

    private fun initAdapters() {
        val items: List<String> = products.list.map { it.name }
        val productsAdapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.tiProductType.editText as? AutoCompleteTextView)?.setAdapter(productsAdapter)
        //----------------------------------------------------------------------------
        val ingredientList = products.list[0].ingredients!!
        ingredientAdapter = IngredientsAdapter(ingredientList) {

        }
        binding.rvProductIngredients.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rvProductIngredients.adapter = ingredientAdapter
        //-----------------------------------------------------------------------------
        val others = products.otherList
        val otherAdapter = ArrayAdapter(requireContext(), R.layout.list_item, others)
        (binding.tiProductOther.editText as? AutoCompleteTextView)?.setAdapter(otherAdapter)
        //-----------------------------------------------------------------------------
        val extras = products.extras
        extraAdapter = ExtraAdapter(extras) {

        }
        binding.rvProductExtras.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rvProductExtras.adapter = extraAdapter
    }

    companion object {
        const val TAG = "ModalBottomSheetDialog"
    }
}