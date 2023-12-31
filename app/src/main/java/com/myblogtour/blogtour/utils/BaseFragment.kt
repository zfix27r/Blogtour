package com.myblogtour.blogtour.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewBinding>(
    private val inflaterBinding: (inflater: LayoutInflater, root: ViewGroup?, attachToRoot: Boolean) -> T,
) : Fragment() {

    private var _binding: T? = null
    val binding: T
        get() {
            return _binding!!
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = inflaterBinding.invoke(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}