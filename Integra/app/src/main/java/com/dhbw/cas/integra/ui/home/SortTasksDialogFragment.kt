package com.dhbw.cas.integra.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.dhbw.cas.integra.R

class SortTasksDialogFragment : DialogFragment() {
    private lateinit var listener: SortDialogListener

    interface SortDialogListener {
        fun onSortDialogPositiveClick(dialog: DialogFragment)
    }

    // instantiate the SortDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = targetFragment as SortDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement SortDialogListener")
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.button_sort_tasks)
                .setView(R.layout.dialog_sort_tasks)
                .setPositiveButton(R.string.okay) { _, _ ->
                    listener.onSortDialogPositiveClick(this)
                }
                .setNegativeButton(R.string.cancel, null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}