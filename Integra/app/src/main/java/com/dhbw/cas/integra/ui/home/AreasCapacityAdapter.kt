package com.dhbw.cas.integra.ui.home

import com.dhbw.cas.integra.R
import android.content.Context
import android.text.Editable
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import com.dhbw.cas.integra.data.Area
import kotlinx.android.synthetic.main.item_area_capacity.view.*

class AreasCapacityAdapter() :
    RecyclerView.Adapter<AreasCapacityAdapter.AreasCapacityViewHolder>() {
    private lateinit var context: Context
    private var areas = emptyList<Area>()

    inner class AreasCapacityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // get screen elements
        val areaText: TextView = itemView.new_sprint_area_text
        private val areaCapacity: EditText = itemView.area_capacity

        init {
            areaCapacity.addTextChangedListener(TextWatcherEditText(areaCapacity))
        }

        private inner class TextWatcherEditText(
            private val editText: EditText
        ) : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    editText.requestFocus()
                    editText.setText("0")
                } else {
                    val currArea = areas.find { it.text == areaText.text.toString().dropLast(1) }
                    currArea!!.totalCapacity = editText.text.toString().toInt()
                    currArea.remainingCapacity = editText.text.toString().toInt()
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreasCapacityViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_area_capacity, parent, false)
        return AreasCapacityViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: AreasCapacityViewHolder, position: Int) {
        with(areas[position]) {
            viewHolder.areaText.text = "$text:"
        }
    }

    override fun getItemCount() = areas.size

    fun setAreas(areas: List<Area>) {
        this.areas = areas
        notifyDataSetChanged()
    }

    fun getAreas(): List<Area> {
        return this.areas
    }

}