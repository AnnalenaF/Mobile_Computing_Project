package com.dhbw.cas.integra.ui.home

import com.dhbw.cas.integra.R
import android.content.Context
import android.text.Editable
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.view.ActionMode
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_area.view.*
import android.text.InputType.*
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.dhbw.cas.integra.data.Area
import com.dhbw.cas.integra.ui.areas.AreasViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.item_area.view.area_text
import kotlinx.android.synthetic.main.item_area_capa_avail.view.*
import kotlinx.android.synthetic.main.item_area_capacity.view.*
import org.w3c.dom.Text

class AreasCapacityAvailAdapter() :
    RecyclerView.Adapter<AreasCapacityAvailAdapter.AreasCapacityAvailViewHolder>() {
    private lateinit var context : Context
    private var areas = emptyList<Area>()

    inner class AreasCapacityAvailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // get screen element
        val areaCapaLeft: TextView = itemView.area_capa

        init {

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
            val remainingCapacityInMinutes = remainingCapacity!! * 60
            viewHolder.areaCapaLeft.text = remainingCapacityInMinutes.toString()
            viewHolder.areaCapaLeft.setBackgroundResource(label)
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