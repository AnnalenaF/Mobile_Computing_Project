package com.dhbw.cas.integra.ui.areas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.databinding.FragmentAreasBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class AreasFragment : Fragment() {

    private lateinit var areasViewModel: AreasViewModel
    private lateinit var binding: FragmentAreasBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        areasViewModel =
                ViewModelProvider(this).get(AreasViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_areas, container, false)
        val root = binding.getRoot()
        //val root = inflater.inflate(R.layout.fragment_areas, container, false)
        val recyclerView: RecyclerView = binding.areasList
        val areasAdapter = AreasAdapter()
        areasAdapter.setAreas(areasViewModel.areas)
        recyclerView.adapter = areasAdapter
        //areasViewModel.text.observe(viewLifecycleOwner, Observer {
            //textView.text = it
        //})
        val fab: FloatingActionButton = binding.actionAddArea
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        return root
    }
}