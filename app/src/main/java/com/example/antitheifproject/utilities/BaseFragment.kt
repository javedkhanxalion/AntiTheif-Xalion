package com.example.antitheifproject.utilities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewBinding>(private val bindingInflater: (layoutInflater:LayoutInflater) -> T) :
    Fragment() {

    // Bindings
    var _binding: T? = null
    /**
     * Binding
     */
    val binding get() = _binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = bindingInflater.invoke(inflater)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun onBackPressed(): Boolean {
        // Return false if you don't want to consume the back press event in the fragment
        // Return true if the back press event is consumed by the fragment
        return false
    }
}