package com.dhbw.cas.integra.ui.catalogue

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.dhbw.cas.integra.R

class CreateTaskDialogFragment : DialogFragment() {
    private lateinit var listener: CreateTaskDialogListener
    private lateinit var dialogView: View

    companion object {
        private var staticSpinnerAdapter: ArrayAdapter<String>? = null
    }

    interface CreateTaskDialogListener {
        fun onCreateDialogPositiveClick(dialog: DialogFragment, view: View)
    }

    // instantiate the CreateTaskDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = targetFragment as CreateTaskDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement CreateTaskDialogListener")
            )
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(activity)
            dialogView = layoutInflater.inflate(R.layout.dialog_new_task, null )
            builder.setTitle(R.string.new_task)
                .setView(dialogView)
                .setPositiveButton(R.string.okay) { _, _ ->
                    listener.onCreateDialogPositiveClick(this, dialogView)
                }
                .setNegativeButton(R.string.cancel, null)

            if (staticSpinnerAdapter != null) {
                dialogView.findViewById<Spinner>(R.id.new_task_area_spinner).adapter =
                    staticSpinnerAdapter
            }

            return builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setSpinnerAdapter(spinnerAdapter: ArrayAdapter<String>) {
        staticSpinnerAdapter = spinnerAdapter
        dialogView.findViewById<Spinner>(R.id.new_task_area_spinner).adapter =
            spinnerAdapter
    }

}