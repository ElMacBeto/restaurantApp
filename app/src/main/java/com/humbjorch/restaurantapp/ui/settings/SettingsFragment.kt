package com.humbjorch.restaurantapp.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.humbjorch.restaurantapp.core.utils.printer.PrinterUtils
import com.humbjorch.restaurantapp.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var printerUtils: PrinterUtils
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setView()
    }

    private fun setView() {
        binding.tiPrinterAddress.editText!!.setText(viewModel.getPrinterAddress())
        binding.tiPrinterPort.editText!!.setText(viewModel.getPrinterPort().toString())
    }


    private fun setListeners() {
       binding.btnSave.setOnClickListener {
           val printerAddress = binding.tiPrinterAddress.editText!!.text.toString()
           val printerPort = binding.tiPrinterPort.editText!!.text.toString()
           viewModel.savePrinter(printerAddress, printerPort)
           val action = SettingsFragmentDirections.actionSettingsFragmentToHomeFragment()
           findNavController().navigate(action)
       }
    }

}