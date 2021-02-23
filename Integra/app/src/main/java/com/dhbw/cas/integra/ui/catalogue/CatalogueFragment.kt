package com.dhbw.cas.integra.ui.catalogue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R

class CatalogueFragment : Fragment() {

    private lateinit var catalogueViewModel: CatalogueViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        catalogueViewModel =
                ViewModelProvider(this).get(CatalogueViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_catalogue, container, false)

        // get recycler view containing task list, set adapter and observe tasks
        val recyclerView: RecyclerView = root.findViewById(R.id.task_list)
        val catalogueAdapter = CatalogueAdapter()
        recyclerView.adapter = catalogueAdapter
        val main : AppCompatActivity = activity as AppCompatActivity
        catalogueViewModel.tasks.observe(main) { tasks -> catalogueAdapter.setTasks(tasks) }

        // add divider to recycler view list
        val layoutManager : LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(
                recyclerView.context,
                layoutManager.orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        return root
    }
}