package com.dhbw.cas.integra.ui.catalogue.task

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.data.Area
import com.dhbw.cas.integra.ui.areas.AreasViewModel
import com.dhbw.cas.integra.ui.catalogue.CatalogueViewModel
import com.dhbw.cas.integra.data.Task
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
    private lateinit var areasList: List<Area>
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
        // set adapter for areas spinner
        areasViewModel.getAreaTexts().observe(viewLifecycleOwner, { spinnerData ->
            val spinnerAdapter = ArrayAdapter(
                view.context,
                android.R.layout.simple_spinner_item,
                spinnerData
            )
            areaSpinner.adapter = spinnerAdapter
        })

        // observe areas
        areasViewModel.areas.observe(viewLifecycleOwner, { areas ->
            areasList = areas
        })
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

        //set validation listener for title and duration
        title.addTextChangedListener(TextWatcherEditText(title, getString(R.string.task_title_empty_error)))
        expectedDuration.addTextChangedListener(TextWatcherEditText(expectedDuration, getString(R.string.task_duration_empty_error)))
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
                error = null
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
                val correct = validateTask()
                if (correct){
                    val catalogueViewModel =
                        ViewModelProvider(this).get(CatalogueViewModel::class.java)
                    var prio = 0
                    if (priority.text.toString().isNotEmpty()){
                        prio = priority.text.toString().toInt()
                    }
                    catalogueViewModel.updateTask(
                        Task(title.text.toString(),
                                                       description.text.toString(),
                                                       prio,
                                                       areaSpinner.selectedItem as String,
                                                       expectedDuration.text.toString().toInt(),
                                                       args.loggedDuration,
                                                       args.id)
                    )
                    switchEditMode()
                    areaDisplay.text = areaSpinner.selectedItem as String
                    val selectedArea = areasList.find { it.text == areaDisplay.text }
                    areaDisplay.setBackgroundResource(selectedArea!!.label)
                }
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

            // get area of current task and set it as selected
            val numOfAreas = areasList.size
            for (i in 0 until numOfAreas){
                val areaText = areaSpinner.adapter.getItem(i) as String
                if (areaText == areaDisplay.text.toString()){
                    areaSpinner.setSelection(i)
                }
            }
        }
        requireActivity().invalidateOptionsMenu()
    }

    private fun setValuesFromArgs() {
        title.setText(args.title)
        areaDisplay.text = args.areaText
        areaDisplay.setBackgroundResource(args.areaLabel)
        priority.setText(args.priority.toString())
        expectedDuration.setText(args.expectedDuration.toString())
        description.setText(args.description)
    }

    private inner class TextWatcherEditText(
        private val editText: EditText, private val errMessage: String): TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // do nothing
        }

        override fun afterTextChanged(s: Editable?) {
            if (s.toString().isEmpty()) {
                editText.requestFocus()
                editText.error = errMessage
            }
        }

    }
    private fun validateTask(): Boolean{
        return when {
            title.error != null  -> {
                false
            }
            expectedDuration.error != null -> {
                false
            }
            else -> {
                true
            }
        }
    }
}