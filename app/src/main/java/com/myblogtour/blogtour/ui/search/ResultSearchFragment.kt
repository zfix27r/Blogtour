package com.myblogtour.blogtour.ui.search

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.myblogtour.blogtour.appState.AppStateSearchPublication
import com.myblogtour.blogtour.databinding.FragmentResultSearchBinding
import com.myblogtour.blogtour.databinding.ItemRecyclerViewResultSearchBinding
import com.myblogtour.blogtour.utils.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResultSearchFragment :
    BaseFragment<FragmentResultSearchBinding>(FragmentResultSearchBinding::inflate),
    OnMyClickListener {

    private val viewModelResult: ResultSearchViewModel by viewModel()
    private val adapter: ResultRecyclerAdapter by lazy {
        ResultRecyclerAdapter(this)
    }
    private var flagMoreMenu = false
    private var flagTextSearch = true
    private lateinit var itemOpenMenuMore: ItemRecyclerViewResultSearchBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewResultSearch.adapter = adapter
        viewModelResult.getLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is AppStateSearchPublication.OnError -> {
                    flagTextSearch = true
                    binding.textInputLayout.error = it.onError
                }
                is AppStateSearchPublication.OnSuccess -> {
                    with(binding) {
                        progressBarResultSearch.visibility = View.GONE
                    }
                    closeKeyBoard()
                    adapter.setListPublication(it.onSuccess)
                }
                is AppStateSearchPublication.OpenSearchFragment -> {
                    with(binding) {
                        progressBarResultSearch.visibility = View.VISIBLE
                    }
                }
                is AppStateSearchPublication.OnErrorLike -> {
                    Toast.makeText(requireActivity(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
        initSearch()
    }

    private fun initSearch() {
        with(binding) {
            textInputLayout.setEndIconOnClickListener {
                viewModelResult.setSearchPublication(editTextSearch.text)
            }
            editTextSearch.setOnEditorActionListener { _, actionId, _ ->
                var consumed = false
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    viewModelResult.setSearchPublication(editTextSearch.text)
                    closeKeyBoard()
                    consumed = true
                }
                return@setOnEditorActionListener consumed
            }
            editTextSearch.addTextChangedListener {
                if (flagTextSearch){
                    it?.let {
                        textInputLayout.error = null
                        flagTextSearch = false
                    }
                }
            }
        }
    }

    private fun closeKeyBoard() {
        requireActivity().currentFocus?.let { view ->
            val imm =
                requireActivity().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onItemClickLike(idTableLike: String) {
        viewModelResult.likePublication(idTableLike)
    }

    override fun onItemClickComplaintPublication(id: String) {
        viewModelResult.updatePublicationComplaint(id, "recWOHgeC8AlH04cD")
    }

    override fun onItemClickMore(item: ItemRecyclerViewResultSearchBinding) {
        moreMenuPublication(item)
    }

    private fun moreMenuPublication(item: ItemRecyclerViewResultSearchBinding) {
        flagMoreMenu = !flagMoreMenu
        itemOpenMenuMore = item
        if (flagMoreMenu) {
            itemOpenMenuMore.moreMenuPublicationResultSearch.visibility = View.VISIBLE
            ObjectAnimator.ofFloat(
                itemOpenMenuMore.moreMenuPublicationResultSearch,
                View.TRANSLATION_Y,
                0f,
                130f
            )
                .setDuration(1000).start()
            itemOpenMenuMore.moreMenuPublicationResultSearch.animate().alpha(1f).setDuration(1000)
                .setListener(
                    object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            itemOpenMenuMore.moreMenuPublicationResultSearch.isClickable = true
                            super.onAnimationEnd(animation)
                        }
                    }
                )
        } else {
            ObjectAnimator.ofFloat(
                itemOpenMenuMore.moreMenuPublicationResultSearch,
                View.TRANSLATION_Y,
                130f,
                0f
            )
                .setDuration(1000).start()
            itemOpenMenuMore.moreMenuPublicationResultSearch.animate().alpha(0f).setDuration(1000)
                .setListener(
                    object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            itemOpenMenuMore.moreMenuPublicationResultSearch.visibility = View.GONE
                            itemOpenMenuMore.moreMenuPublicationResultSearch.isClickable = false
                            super.onAnimationEnd(animation)
                        }
                    }
                )
        }
    }
}