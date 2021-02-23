package com.dhbw.cas.integra.ui.catalogue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
        val textView: TextView = root.findViewById(R.id.text_gallery)
        catalogueViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        return root
    }
}