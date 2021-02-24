package com.dhbw.cas.integra.ui.areas

import com.dhbw.cas.integra.R
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.view.ActionMode
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_area.view.*
import android.text.InputType.*
import android.widget.EditText
import android.widget.Spinner
import com.google.android.material.snackbar.Snackbar

class AreasAdapter(private val view: View,
                   private val areasViewModel: AreasViewModel,
                   private val activity: AppCompatActivity) :
    RecyclerView.Adapter<AreasAdapter.AreasViewHolder>(), ActionMode.Callback {
    private lateinit var context : Context
    private var areas = emptyList<Area>()
    private var multiSelect = false
    private val selectedItems = arrayListOf<Area>()
    private val labelArray = getLabelArray(true)

    inner class AreasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // get screen elements
        val areaText: EditText = itemView.area_text
        val areaLabelSpinner: Spinner = itemView.area_label_spinner
        private val areaEditButton = itemView.area_edit_button
        private val areaCancelEditButton = itemView.area_edit_cancel_button

        // instantiate private variables
        private var areaTextValue : CharSequence = ""
        private var areaLabelSelected : Int = 0
        private var editMode = false
        // remember text background containing underline
        private val areaTextBackground = areaText.background

        init {
            //initially color label spinner is disabled and text does not show underline background
            areaLabelSpinner.isEnabled = false
            areaText.background = null
            // set adapter for label spinner with complete color label list
            areaLabelSpinner.adapter = AreaLabelSpinnerAdapter( itemView.context, labelArray)

            areaEditButton.setOnClickListener { itemViewEdit ->
                if (!editMode) { //edit button pressed

                    switchDisplayEditMode(itemViewEdit.context)

                    //remember current values to return to when cancel editing
                    areaTextValue = areaText.text.toString()
                    areaLabelSelected = areaLabelSpinner.selectedItemPosition

                } else { //changes shall be applied
                    val pos = adapterPosition
                    val areaToBeChanged = areas[pos]
                    when {
                        //check area text is not empty and display error directly on text field
                        areaText.text.toString().isEmpty() -> {
                            areaText.requestFocus()
                            areaText.error = context.getString(R.string.area_text_empty_error)
                        }
                        //check area text is not already used by other area and display error directly on text field
                        areas.find {(it.text == areaText.text.toString()) && it.text != areaToBeChanged.text } != null -> {
                            areaText.requestFocus()
                            areaText.error = context.getString(R.string.area_text_not_unique_error)
                        }
                        //check area label is not already used by other area and display error using snackbar
                        areas.find {(it.label == areaLabelSpinner.selectedItem as Int) && (it.text != areaToBeChanged.text)} != null -> {
                            val snackbarError = Snackbar.make(view, R.string.area_label_not_unique_error, Snackbar.LENGTH_LONG)
                            snackbarError.setTextColor(ContextCompat.getColor(context, R.color.red_error))
                            snackbarError.show()
                        }
                        else -> { //no error found --> apply changes and leave edit mode
                            switchDisplayEditMode(itemViewEdit.context)

                            //save changes
                            areaToBeChanged.text = areaText.text.toString()
                            areaToBeChanged.label = areaLabelSpinner.selectedItem as Int
                            areasViewModel.updateArea(areas[pos])
                            notifyDataSetChanged()
                        }
                    }
                }
            }

            areaCancelEditButton.setOnClickListener { itemViewCanc ->
                //set text and label to old values and leave edit mode
                areaLabelSpinner.setSelection(areaLabelSelected)
                areaText.setText(areaTextValue)
                switchDisplayEditMode(itemViewCanc.context)
            }
        }

        private fun switchDisplayEditMode(context: Context) {
            if (!editMode) { //switch to Edit mode
                editMode = true
                areaLabelSpinner.isEnabled = true
                areaText.apply {
                    inputType = TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    isClickable = true
                    isCursorVisible = true
                    isFocusable = true
                    isFocusableInTouchMode = true
                    background = areaTextBackground
                }
                //change icon on button and display cancel button
                areaEditButton.setImageDrawable(getDrawable(context, R.drawable.ic_baseline_check_24))
                areaCancelEditButton.visibility = View.VISIBLE
            } else { // switch to display mode
                editMode = false
                areaLabelSpinner.isEnabled = false
                areaText.apply {
                    inputType = TYPE_NULL
                    isClickable = false
                    isCursorVisible = false
                    isFocusable = false
                    isFocusableInTouchMode = false
                    background = null
                }
                //change icon on button and hide cancel button
                areaEditButton.setImageDrawable(getDrawable(context, R.drawable.ic_baseline_edit_24))
                areaCancelEditButton.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreasViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_area, parent, false)
        return AreasViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: AreasViewHolder, position: Int) {
        with(areas[position]) {
            viewHolder.areaText.setText(text)
            viewHolder.areaLabelSpinner.setSelection(labelArray.indexOf(label))
        }

        val currentArea = areas[position]
        // for every item, check to see if it exists in the selected items array
        if (selectedItems.contains(currentArea)) {
            // if the item is selected, let the user know by adding a dark layer above it
            viewHolder.itemView.alpha = 0.3f
        } else { // else, keep it as it is
            viewHolder.itemView.alpha = 1.0f
        }

        // start multi selection when long pressing an area and display context menu for deletion
        viewHolder.itemView.setOnLongClickListener {
            if (!multiSelect) {
                multiSelect = true
                // add area to list of selected items
                selectItem(viewHolder, currentArea)
                // show context menu
                val appCompatActivity : AppCompatActivity = activity
                appCompatActivity.startSupportActionMode(this)
                true
            } else
                false
        }

        // if area is clicked in multi selection add it to list of selected items
        viewHolder.itemView.setOnClickListener {
            if (multiSelect)
                selectItem(viewHolder, currentArea)
        }
    }

    override fun getItemCount() = areas.size

    // area deletion
    override fun onActionItemClicked(
        mode: ActionMode?,
        item: MenuItem?
    ): Boolean {
        if (item?.itemId == R.id.action_multi_delete) {
            if (areas.size - selectedItems.size == 0) { // prevent deleting all areas
                val snackbarError = Snackbar.make(view, R.string.message_area_no_deletion, Snackbar.LENGTH_LONG)
                snackbarError.setTextColor(ContextCompat.getColor(context, R.color.red_error))
                snackbarError.show()
            } else { // delete selected area(s)
                val areasDel = mutableListOf<Area>()
                for (selItem in selectedItems) {
                    areasDel.add(selItem)
                    areasViewModel.deleteArea(selItem)
                }

                // show success message containing number of deleted areas
                val message : String = if (selectedItems.size == 1) {
                    context.getString(R.string.message_area_deleted, 1)
                } else {
                    context.getString(R.string.message_areas_deleted, selectedItems.size)
                }
                val snackbarSuccess = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                // enable undo action on success message snackbar
                snackbarSuccess.setAction(R.string.action_undo) {
                    for (areaDel in areasDel) {
                        areasViewModel.createArea(text = areaDel.text, label = areaDel.label)
                    }
                }
                snackbarSuccess.show()
                mode?.finish()
            }
        }
        return true
    }

    override fun onCreateActionMode(
        mode: ActionMode,
        menu: Menu?
    ): Boolean {
        // hide main action bar and inflate context menu for deletion
        activity.supportActionBar?.hide()
        val inflater: MenuInflater = mode.menuInflater
        inflater.inflate(R.menu.multiselect_menu, menu)
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        // show main action bar again when context menu for deletion is closed
        activity.supportActionBar?.show()
        // end multi selection mode and clear list of selected areas
        multiSelect = false
        selectedItems.clear()
        notifyDataSetChanged()
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    fun setAreas(areas: List<Area>) {
        this.areas = areas
        notifyDataSetChanged()
    }

    fun getAreas() : List<Area> {
        return this.areas
    }

    fun getLabelArray(completeList: Boolean = false): ArrayList<Int> {
        val labelArrayList = arrayListOf(
            R.drawable.shape_area_label_0, R.drawable.shape_area_label_1,
            R.drawable.shape_area_label_2, R.drawable.shape_area_label_3,
            R.drawable.shape_area_label_4, R.drawable.shape_area_label_5,
            R.drawable.shape_area_label_6, R.drawable.shape_area_label_7,
            R.drawable.shape_area_label_8, R.drawable.shape_area_label_9
        )
        if (!completeList) {
            for (area in areas) {
                labelArrayList.remove(area.label)
            }
        }
        return labelArrayList
    }

    private fun selectItem(viewHolder: AreasViewHolder, area: Area) {
        if (selectedItems.contains(area)) {
            selectedItems.remove(area)
            viewHolder.itemView.alpha = 1.0f
        } else {
            selectedItems.add(area)
            viewHolder.itemView.alpha = 0.3f
        }
    }

}