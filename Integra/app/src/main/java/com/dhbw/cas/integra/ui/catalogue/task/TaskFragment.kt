package com.dhbw.cas.integra.ui.catalogue.task

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.*
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


class TaskFragment : Fragment() {
    private lateinit var areasViewModel: AreasViewModel
    private lateinit var backgroundEdit: Drawable
    private var editMode: Boolean = false
    private lateinit var title: EditText
    private lateinit var areaSpinner : Spinner
    private lateinit var areaDisplay : MaterialTextView
    private lateinit var areaLabel : MaterialTextView
    private lateinit var priority : EditText
    private lateinit var expectedDuration : EditText
    private lateinit var description : EditText
    private lateinit var args: TaskFragmentArgs
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
        title = view.findViewById(R.id.task_title)
        areaSpinner = view.findViewById(R.id.task_area_spinner)
        areaDisplay = view.findViewById(R.id.task_area)
        priority = view.findViewById(R.id.task_prio)
        expectedDuration = view.findViewById(R.id.task_duration)
        description = view.findViewById(R.id.task_descr)
        val argsLocal: TaskFragmentArgs by navArgs()
        args = argsLocal
        title.setText(args.title)
        // set adapter for areas spinner
        areasViewModel.getAreaTexts().observe(viewLifecycleOwner, { spinnerData ->
            val spinnerAdapter = ArrayAdapter(
                view.context,
                android.R.layout.simple_spinner_item,
                spinnerData
            )
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
        setValuesFromArgs()

        // disable editing
        areaLabel = view.findViewById(R.id.task_area_label)
        areaLabel.isVisible = false
        areaSpinner.isVisible = false
        backgroundEdit = title.background
        switchEditModeForText(title)
        switchEditModeForText(priority)
        switchEditModeForText(expectedDuration)
        switchEditModeForText(description, true)
    }

    private fun switchEditModeForText(editText: EditText, multiline: Boolean = false) {
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
                if (multiline) {
                    inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                    isSingleLine = false
                    minLines = 1
                    maxLines = 10
                } else {
                    inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                }
                isClickable = true
                isCursorVisible = true
                isFocusable = true
                isFocusableInTouchMode = true
                background = backgroundEdit
            }
        }
    }

    // enable fragment to display options menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val editAction = menu.findItem(R.id.action_edit)
        val cancelAction = menu.findItem(R.id.action_cancel)
        val saveAction = menu.findItem(R.id.action_save)
        val settings = menu.findItem(R.id.action_settings)
        settings.isVisible = false
        if (editMode){
            editAction.isVisible = false
            cancelAction.isVisible = true
            saveAction.isVisible = true
        } else {
            editAction.isVisible = true
            cancelAction.isVisible = false
            saveAction.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                switchEditMode()
                true
            }
            R.id.action_save -> {
                // TODO validate
                // TODO save
                switchEditMode()
                true
            }
            R.id.action_cancel -> {
                setValuesFromArgs()
                switchEditMode()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun switchEditMode() {
        if (editMode) {
            editMode = false
            areaLabel.isVisible = false
            areaSpinner.isVisible = false
            areaDisplay.isVisible = true
            switchEditModeForText(title)
            switchEditModeForText(priority)
            switchEditModeForText(expectedDuration)
            switchEditModeForText(description, true)
        } else {
            editMode = true
            areaLabel.isVisible = true
            areaSpinner.isVisible = true
            areaDisplay.isVisible = false
            switchEditModeForText(title)
            switchEditModeForText(priority)
            switchEditModeForText(expectedDuration)
            switchEditModeForText(description, true)
        }
        requireActivity().invalidateOptionsMenu()
    }

    private fun setValuesFromArgs() {
        areaDisplay.setText(args.areaText)
        areaDisplay.setBackgroundResource(args.areaLabel)
        priority.setText(args.priority.toString())
        expectedDuration.setText(args.expectedDuration.toString())
        description.setText(args.description)
    }
}