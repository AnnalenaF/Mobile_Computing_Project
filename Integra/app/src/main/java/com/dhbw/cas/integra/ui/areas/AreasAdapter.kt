package com.dhbw.cas.integra.ui.areas

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.dhbw.cas.integra.R
import android.view.ViewGroup
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.item_area.view.*

class AreasAdapter :
    RecyclerView.Adapter<AreasAdapter.AreasViewHolder>() {
    private var areas = emptyList<Area>()

    inner class AreasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.area_text
        val labelView = itemView.area_label
        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreasViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_area, parent, false)
        return AreasViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: AreasViewHolder, position: Int) {
        with(areas[position]) {
            viewHolder.textView.text = text
            viewHolder.labelView.setBackgroundResource(label)
        }
    }

    override fun getItemCount() = areas.size

    fun setAreas(areas: List<Area>) {
        this.areas = areas
        notifyDataSetChanged()
    }

}