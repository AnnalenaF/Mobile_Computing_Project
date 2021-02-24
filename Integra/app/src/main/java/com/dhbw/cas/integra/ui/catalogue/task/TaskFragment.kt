package com.dhbw.cas.integra.ui.catalogue.task

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.ui.areas.AreasViewModel
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.dialog_new_task.*

class TaskFragment : Fragment() {
    private lateinit var areasViewModel: AreasViewModel
    private lateinit var backgroundEdit: Drawable
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_task, container, false)
        areasViewModel =
            ViewModelProvider(this).get(AreasViewModel::class.java)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val title: EditText = view.findViewById(R.id.task_title)
        val areaSpinner : Spinner = view.findViewById(R.id.task_area_spinner)
        val areaDisplay : MaterialTextView = view.findViewById(R.id.task_area)
        val priority : EditText = view.findViewById(R.id.task_prio)
        val expectedDuration : EditText = view.findViewById(R.id.task_duration)
        val description : EditText = view.findViewById(R.id.task_descr)
        val args: TaskFragmentArgs by navArgs()
        title.setText(args.title)
        // set adapter for areas spinner
        areasViewModel.getAreaTexts().observe(viewLifecycleOwner, { spinnerData ->
            val spinnerAdapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, spinnerData)
            areaSpinner.adapter = spinnerAdapter
        })
        // get area of current task and set it as selected
        /*val numOfAreas = areaSpinner.adapter.count
        for (i in 0 until numOfAreas){
            val area = areaSpinner.adapter.getItem(i) as Area
            if (area.text == args.areaText){
                areaSpinner.setSelection(i)
            }
        }*/
        areaDisplay.setText(args.areaText)
        areaDisplay.setBackgroundResource(args.areaLabel)
        priority.setText(args.priority.toString())
        expectedDuration.setText(args.expectedDuration.toString())
        description.setText(args.description)

        // disable editing
        val areaLabel : MaterialTextView = view.findViewById(R.id.task_area_label)
        areaLabel.isVisible = false
        areaSpinner.isVisible = false
        backgroundEdit = title.background
        switchEditMode(title)
        switchEditMode(priority)
        switchEditMode(expectedDuration)
        switchEditMode(description)
    }

    private fun switchEditMode(editText: EditText) {
        if (editText.inputType != InputType.TYPE_NULL){
            editText.apply {
                inputType = InputType.TYPE_NULL
                isClickable = false
                isCursorVisible = false
                isFocusable = false
                isFocusableInTouchMode = false
                background = null
            }
        } else {
            editText.apply {
                inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                isClickable = true
                isCursorVisible = true
                isFocusable = true
                isFocusableInTouchMode = true
                background = backgroundEdit
            }
        }
    }
}