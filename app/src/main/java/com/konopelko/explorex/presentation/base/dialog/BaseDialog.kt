package com.konopelko.explorex.presentation.base.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.konopelko.explorex.R
import com.konopelko.explorex.databinding.DialogBaseBinding

class BaseDialog(
    private val title: String,
    private val message: String,
    private val buttonText: String,
    private val onButtonClickListener: (DialogFragment) -> Unit
): DialogFragment() {

    private lateinit var binding: DialogBaseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBaseBinding.inflate(inflater, container, false)
        binding.title = title
        binding.message = message
        binding.buttonText = buttonText
        setWindowView()
        setOnButtonClickListener()
        return binding.root
    }

    private fun setWindowView() {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.shape_white_cornered_32dp)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setOnButtonClickListener() {
        binding.baseDialogButton.setOnClickListener {
            onButtonClickListener(this)
        }
    }

    companion object {

        const val TAG = "dialog.base"
    }
}