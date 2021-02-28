package com.dhbw.cas.integra.ui.sprint.newsprint.tasks

import com.dhbw.cas.integra.R
import android.content.Context
import android.text.Editable
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import android.text.TextWatcher
import android.widget.TextView
import com.dhbw.cas.integra.data.Area
import kotlinx.android.synthetic.main.item_area_capa_avail.view.*

class AreasCapacityAvailAdapter() :
    RecyclerView.Adapter<AreasCapacityAvailAdapter.AreasCapacityAvailViewHolder>() {
    private lateinit var context : Context
    private var areas = emptyList<Area>()

    inner class AreasCapacityAvailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // get screen element
        val areaCapaLeft: TextView = itemView.area_capa

        init {
            areaCapaLeft.addTextChangedListener(TextWatcherTextView(areaCapaLeft))
        }

        private inner class TextWatcherTextView(
            private val textView: TextView
        ) : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().toInt() < 0) {
                    textView.requestFocus()
                    textView.error = context.getString(R.string.capacity_negative)
                } else {
                    textView.error = null
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreasCapacityAvailViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_area_capa_avail, parent, false)
        return AreasCapacityAvailViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: AreasCapacityAvailViewHolder, position: Int) {
        with(areas[position]) {
            val remainingCapacityInMinutes = remainingCapacity!!
            viewHolder.areaCapaLeft.text = remainingCapacityInMinutes.toString()
            viewHolder.areaCapaLeft.setBackgroundResource(label)
        }
    }

    override fun getItemCount() = areas.size

    fun setAreas(areas: List<Area>) {
        this.areas = areas
        notifyDataSetChanged()
    }
}