package com.dhbw.cas.integra.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.DialogFragment
import com.dhbw.cas.integra.R
import com.google.android.material.checkbox.MaterialCheckBox

class FinishSprintDialogFragment() : DialogFragment() {
    private lateinit var listener: FinishSprintDialogListener

    interface FinishSprintDialogListener {
        fun onFinishDialogPositiveClick(dialog: DialogFragment, view: View)
    }

    // instantiate the FinishSprintDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = targetFragment as FinishSprintDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement FinishSprintDialogListener")
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(activity)
            val view = layoutInflater.inflate(R.layout.dialog_finish_sprint, null)
            builder.setTitle(R.string.finish_sprint)
                .setView(view)
                .setPositiveButton(R.string.finish_sprint) { _, _ ->
                    listener.onFinishDialogPositiveClick(this, view)
                }
                .setNegativeButton(R.string.cancel, null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}