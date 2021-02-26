package com.dhbw.cas.integra.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.data.SprintWithTasks

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var root: View
    private var activeSprint: Any? = Any()
    private var activeSprintLive: Any? = Any()
    private var sprintActive: Boolean = false

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        activeSprint = homeViewModel.activeSprint
        sprintActive = activeSprint is SprintWithTasks
        homeViewModel.activeSprintLive.observe(viewLifecycleOwner, {
            activeSprintLive = it
        })

        if (sprintActive){
            root = inflater.inflate(R.layout.fragment_sprint, container, false)
        } else {
            root = inflater.inflate(R.layout.fragment_no_active_sprint, container, false)

            val buttonPlanSprint = root.findViewById<Button>(R.id.button_plan_sprint)
            buttonPlanSprint.setOnClickListener {
                val action = HomeFragmentDirections.actionNavHomeToNavNewSprint()
                root.findNavController().navigate(action)
            }
        }

        return root
    }
}