package com.dhbw.cas.integra.ui.areas

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.dhbw.cas.integra.R

class CreateAreaDialogFragment() : DialogFragment() {
    private lateinit var listener: CreateAreaDialogListener

    companion object {
        // save labelArray as companion because it survives recreation when orientation changes
        private lateinit var labelArray: ArrayList<Int>
    }

    interface CreateAreaDialogListener {
        fun onCreateDialogPositiveClick(dialog: DialogFragment, view: View)
    }

    // instantiate the CreateAreaDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = targetFragment as CreateAreaDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement CreateAreaDialogListener")
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(activity)
            val view = layoutInflater.inflate(R.layout.dialog_new_area, null)
            builder.setTitle(R.string.new_area)
                .setView(view)
                .setPositiveButton(R.string.okay) { _, _ ->
                    listener.onCreateDialogPositiveClick(this, view)
                }
                .setNegativeButton(R.string.cancel, null)
            // set adapter for label spinner containing only color labels left
            val spinnerAdapter = AreaLabelSpinnerAdapter(view.context, labelArray)
            val areaNewLabelSpinner = view.findViewById<Spinner>(R.id.new_area_label_spinner)
            areaNewLabelSpinner.adapter = spinnerAdapter

            return builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setLabelArray(labelArrayNew: ArrayList<Int>){
        labelArray = labelArrayNew
    }
}