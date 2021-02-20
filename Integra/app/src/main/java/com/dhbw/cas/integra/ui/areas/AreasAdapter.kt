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

    inner class AreasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val areaText = itemView.area_text
        val areaLabel = itemView.area_label
        val areaEditButton = itemView.area_edit_button
        val areaCancelEditButton = itemView.area_edit_cancel_button
        var areaTextValue : CharSequence = ""
        init {
            var editMode : Boolean = false
            var areaTextBackground = areaText.background
            areaText.background = null
            areaEditButton.setOnClickListener { itemView ->
                if (!editMode) {
                    areaTextValue = areaText.text.toString()
                    editMode = true
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
                } else {
                    editMode = false
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

                    var pos = adapterPosition
                    this@AreasAdapter.areas[pos].text = areaText.text.toString()
                    notifyDataSetChanged()
                }
            }
            areaCancelEditButton.setOnClickListener { itemView ->
                editMode = false
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
            viewHolder.areaLabel.setBackgroundResource(label)
        }
    }

    override fun getItemCount() = areas.size

    fun setAreas(areas: MutableList<Area>) {
        this.areas = areas
        notifyDataSetChanged()
    }

}