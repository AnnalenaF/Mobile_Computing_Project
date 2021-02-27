package com.dhbw.cas.integra.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.data.Task

class LogWorkDialogFragment(private val task: Task) : DialogFragment() {
    private lateinit var listener: LogWorkDialogListener

    interface LogWorkDialogListener {
        fun onLogDialogPositiveClick(dialog: DialogFragment, task: Task)
    }

    // instantiate the SortDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = targetFragment as LogWorkDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement LogWorkDialogListener")
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.action_log_work)
                .setView(R.layout.dialog_log_work)
                .setPositiveButton(R.string.okay) { _, _ ->
                    listener.onLogDialogPositiveClick(this, task)
                }
                .setNegativeButton(R.string.cancel, null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}