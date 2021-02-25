package com.dhbw.cas.integra.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.dhbw.cas.integra.R

class SprintFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_sprint, container, false)
        return root
    }

    /*
    // enable fragment to display options menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.start_sprint_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // do not display settings during splint planning
    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_settings).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_start -> { // save sprint and display current sprint
                /*val action = NewSprintFragmentDirections.actionNavNewSprintToNavNewSprintTasks()
                root.findNavController().navigate(action)*/
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }*/
}