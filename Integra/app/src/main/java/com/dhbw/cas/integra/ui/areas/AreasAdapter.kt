package com.dhbw.cas.integra.ui.areas

import android.text.InputType.TYPE_NULL
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.dhbw.cas.integra.R
import android.view.ViewGroup
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.item_area.view.*
import android.text.InputType.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable

class AreasAdapter :
    RecyclerView.Adapter<AreasAdapter.AreasViewHolder>() {
    private var areas = emptyList<Area>()
    val labelArray = arrayOf(R.drawable.shape_area_label_0, R.drawable.shape_area_label_1,
                             R.drawable.shape_area_label_2, R.drawable.shape_area_label_3,
                             R.drawable.shape_area_label_4, R.drawable.shape_area_label_5,
                             R.drawable.shape_area_label_6, R.drawable.shape_area_label_7,
                             R.drawable.shape_area_label_8, R.drawable.shape_area_label_9)
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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_area, parent, false)
        return AreasViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: AreasViewHolder, position: Int) {
        with(areas[position]) {
            viewHolder.areaText.setText(text)
            viewHolder.areaLabelSpinner.setSelection(labelArray.indexOf(label))
        }
    }

    override fun getItemCount() = areas.size

    fun setAreas(areas: MutableList<Area>) {
        this.areas = areas
        notifyDataSetChanged()
    }

}