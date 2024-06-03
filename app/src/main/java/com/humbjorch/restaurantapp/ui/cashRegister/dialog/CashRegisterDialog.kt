package com.humbjorch.restaurantapp.ui.cashRegister.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.humbjorch.restaurantapp.R
import com.humbjorch.restaurantapp.data.model.CashRegisterModel
import com.humbjorch.restaurantapp.databinding.DialogCashRegisterBinding

enum class CashRegisterType(val value:String){
    INCOME("income"),
    EXPENSES("expenses")
}

class CashRegisterDialog(
    private val cashRegisterType: CashRegisterType,
    private val cashRegisterModel: CashRegisterModel? = null,
    private val actionFunction: (CashRegisterModel) -> Unit
) : DialogFragment() {

    private lateinit var binding: DialogCashRegisterBinding

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogCashRegisterBinding.inflate(layoutInflater)

        if (cashRegisterType == CashRegisterType.INCOME) {
            binding.tvLabelTitleCashRegister.text =
                getText(R.string.label_cash_register_income)
        } else {
            binding.tvLabelTitleCashRegister.text =
                getText(R.string.label_cash_register_expenses)
        }

        if (cashRegisterModel != null){
            binding.tvNameCashRegister.editText!!.setText(cashRegisterModel.name)
            binding.tvValueCashRegister.editText!!.setText(cashRegisterModel.value.toString())
            binding.btnAddCashRegister.text = getText(R.string.label_save_button)
        }

        binding.btnAddCashRegister.setOnClickListener {
            val cashRegisterName = binding.tvNameCashRegister.editText!!.text.toString()
            val cashRegisterValue = binding.tvValueCashRegister.editText!!.text.toString()

            if (cashRegisterName.isNotEmpty() && cashRegisterValue.isNotEmpty()){
                actionFunction.invoke(
                    CashRegisterModel(cashRegisterName, cashRegisterValue.toInt())
                )
                dismiss()
            } else
                Toast.makeText(requireContext(), "campos vacios", Toast.LENGTH_SHORT).show()
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    companion object {
        const val TAG = "DeleteDialog"
    }

}