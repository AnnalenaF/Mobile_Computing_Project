package com.dhbw.cas.integra.ui.areas

import android.content.Context
import android.text.InputType.TYPE_NULL
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R
import kotlinx.android.synthetic.main.item_area.view.*
import android.text.InputType.*
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_areas.view.*

class AreasAdapter(private val view: View,
                   private val areasViewModel: AreasViewModel,
                   private val activity: AppCompatActivity,
                   private val labelArray: Array<Int>) :
    RecyclerView.Adapter<AreasAdapter.AreasViewHolder>(), ActionMode.Callback {
    private var areas = emptyList<Area>()
    private var multiSelect = false
    private val selectedItems = arrayListOf<Area>()
    private lateinit var context : Context
    inner class AreasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val areaText = itemView.area_text
        val areaLabelSpinner = itemView.area_label_spinner
        val areaLabelSpinnerAdapter : AreaLabelSpinnerAdapter = AreaLabelSpinnerAdapter(
            itemView.context, labelArray)
        val areaEditButton = itemView.area_edit_button
        val areaCancelEditButton = itemView.area_edit_cancel_button
        var areaTextValue : CharSequence = ""
        var areaLabelSelected : Int = 0
        init {
            var editMode = false
            val areaTextBackground = areaText.background
            areaText.background = null
            areaLabelSpinner.setAdapter(areaLabelSpinnerAdapter)
            areaLabelSpinner.setEnabled(false)
            areaEditButton.setOnClickListener { itemView ->
                if (!editMode) { //edit button pressed
                    areaTextValue = areaText.text.toString()
                    areaLabelSelected = areaLabelSpinner.selectedItemPosition
                    editMode = true
                    areaLabelSpinner.setEnabled(true)
                    areaText.apply {
                        setInputType(TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                        setClickable(true)
                        setCursorVisible(true)
                        setFocusable(true)
                        setFocusableInTouchMode(true)
                        background = areaTextBackground
                    }
                    areaEditButton.setImageDrawable(getDrawable(itemView.context, R.drawable.ic_baseline_check_24))
                    areaCancelEditButton.visibility = View.VISIBLE
                } else { //apply changes and leave edit mode
                    editMode = false
                    areaLabelSpinner.setEnabled(false)
                    areaText.apply {
                        setInputType(TYPE_NULL)
                        setClickable(false)
                        setCursorVisible(false)
                        setFocusable(false)
                        setFocusableInTouchMode(false)
                        background = null
                    }
                    areaEditButton.setImageDrawable(getDrawable(itemView.context, R.drawable.ic_baseline_edit_24))
                    areaCancelEditButton.visibility = View.GONE

                    val pos = adapterPosition
                    this@AreasAdapter.areas[pos].text = areaText.text.toString()
                    this@AreasAdapter.areas[pos].label = areaLabelSpinner.selectedItem as Int
                    areasViewModel.updateArea(areas[pos])
                    notifyDataSetChanged()
                }
            }
            areaCancelEditButton.setOnClickListener { itemView ->
                editMode = false
                areaLabelSpinner.setEnabled(false)
                areaLabelSpinner.setSelection(areaLabelSelected)
                areaText.apply {
                    setText(areaTextValue)
                    setInputType(TYPE_NULL)
                    setClickable(false)
                    setCursorVisible(false)
                    setFocusable(false)
                    setFocusableInTouchMode(false)
                    background = null
                }
                areaEditButton.setImageDrawable(getDrawable(itemView.context, R.drawable.ic_baseline_edit_24))
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

        // Get the current area
        val currentArea = areas[position]
        // for every item, check to see if it exists in the selected items array
        if (selectedItems.contains(currentArea)) {
            // if the item is selected, let the user know by adding a dark layer above it
            viewHolder.itemView.alpha = 0.3f
        } else {
            // else, keep it as it is
            viewHolder.itemView.alpha = 1.0f
        }

        // set handler to define what happens when an item is long pressed
        viewHolder.itemView.setOnLongClickListener {
            // if multiSelect is false, set it to true and select the item
            if (!multiSelect) {
                // We have started multi selection, so set the flag to true
                multiSelect = true
                // Add it to the list containing all the selected images
                selectItem(viewHolder, currentArea)
                // As soon as the user starts multi-select process, show the contextual menu
                val appCompatActivity : AppCompatActivity = activity
                appCompatActivity.startSupportActionMode(this)
                true
            } else
                false
        }

        // handler to define what happens when an item is tapped
        viewHolder.itemView.setOnClickListener {
            // if the user is in multi-select mode, add it to the multi select list
            if (multiSelect)
                selectItem(viewHolder, currentArea)
        }
    }

    // helper function that adds/removes an item to the list depending on the app's state
    private fun selectItem(viewHolder: AreasViewHolder, area: Area) {
        // If the "selectedItems" list contains the item, remove it and set it's state to normal
        if (selectedItems.contains(area)) {
            selectedItems.remove(area)
            viewHolder.itemView.alpha = 1.0f
        } else {
            // Else, add it to the list and add a darker shade over the item, letting the user know that it was selected
            selectedItems.add(area)
            viewHolder.itemView.alpha = 0.3f
        }
    }

    override fun getItemCount() = areas.size

    fun setAreas(areas: List<Area>) {
        this.areas = areas
        notifyDataSetChanged()
    }

    // Called when a menu item was clicked
    override fun onActionItemClicked(
        mode: ActionMode?,
        item: MenuItem?
    ): Boolean {
        if (item?.itemId == R.id.action_area_delete) {
            // Delete button is clicked, handle the deletion and finish the multi select process
            if (areas.size - selectedItems.size == 0) {
                val snackbarError = Snackbar.make(view, R.string.message_area_no_deletion, Snackbar.LENGTH_LONG)
                snackbarError.setTextColor(ContextCompat.getColor(context, R.color.red_error))
                snackbarError.show()
            } else {
                val areasDel = mutableListOf<Area>()
                //val areasMutable : MutableList<Area> = areas as MutableList<Area>
                for (selItem in selectedItems) {
                    areasDel.add(selItem)
                    areasViewModel.deleteArea(selItem)
                }
                //setAreas(areasMutable)

                val message : String
                if (selectedItems.size == 1) {
                    message = context.getString(R.string.message_area_deleted, 1)
                } else {
                    message = context.getString(R.string.message_areas_deleted, selectedItems.size)
                }
                val snackbarSuccess = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                snackbarSuccess.setAction(R.string.action_undo, {
                    for (areaDel in areasDel) {
                        areasViewModel.createArea(text = areaDel.text, label = areaDel.label)
                    }
                })
                snackbarSuccess.show()
                mode?.finish()
            }
        }
        return true
    }

    // Called when the menu is created i.e. when the user starts multi-select mode (inflate your menu xml here)
    override fun onCreateActionMode(
        mode: ActionMode,
        menu: Menu?
    ): Boolean {
        activity.supportActionBar?.hide()
        // Inflate the menu resource providing context menu items
        val inflater: MenuInflater = mode.menuInflater
        inflater.inflate(R.menu.areas_menu, menu)
        return true
    }

    // Called when the Context ActionBar disappears i.e. when the user leaves multi-select mode
    override fun onDestroyActionMode(mode: ActionMode?) {
        // finished multi selection
        activity.supportActionBar?.show()
        multiSelect = false
        selectedItems.clear()
        notifyDataSetChanged()
    }

    // Called to refresh an action mode's action menu (we won't be using this here)
    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

}