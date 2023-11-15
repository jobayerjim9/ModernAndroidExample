package com.jobaer.example.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jobaer.example.R
import com.jobaer.example.databinding.FragmentMatchesBinding
import com.jobaer.example.models.Match
import com.jobaer.example.ui.MainActivity
import com.jobaer.example.ui.adapters.MatchClickHandler
import com.jobaer.example.ui.adapters.MatchRvAdapter
import com.jobaer.example.ui.viewmodels.MatchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class MatchesFragment : Fragment(), MatchClickHandler {
    private lateinit var binding: FragmentMatchesBinding
    private val matchViewModel: MatchViewModel by viewModels()
    private lateinit var upcomingAdapter: MatchRvAdapter
    private lateinit var previousAdapter: MatchRvAdapter
    private val args: MatchesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_matches, container, false)
        initViews()
        setupObservers()
        return binding.root
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setupObservers() {
        matchViewModel.upcomingMatches.observe(viewLifecycleOwner) {
            if (it != null) {
                upcomingAdapter.submitList(it)
                binding.showNoUpcoming = it.isEmpty() && !matchViewModel.loader.value!!
            }
        }
        matchViewModel.previousMatches.observe(viewLifecycleOwner) {
            if (it != null) {
                previousAdapter.submitList(it)
                binding.showNoPrevious = it.isEmpty() && !binding.swipeRefresh.isRefreshing
            }
        }
        matchViewModel.loader.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.swipeRefresh.isRefreshing = it && !binding.swipeRefresh.isRefreshing
            }
        }
        matchViewModel.message.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                matchViewModel._message.postValue("")
            }
        }
    }


    private fun initViews() {
        // Don't show No data place holder at the beginning
        binding.showNoUpcoming = false
        binding.showNoPrevious = false

        upcomingAdapter = MatchRvAdapter(this)
        binding.upcomingRecycler.adapter = upcomingAdapter

        previousAdapter = MatchRvAdapter(this)
        binding.prevousRecycler.adapter = previousAdapter

        if (args.team != null) {
            when (requireActivity()) {
                is MainActivity -> {
                    (requireActivity() as MainActivity).supportActionBar?.title =
                        args.team!!.name
                    (requireActivity() as MainActivity).showHideBottomNav(false)
                    (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
                        true
                    )
                }
            }
            matchViewModel.getMatchByTeam(args.team!!.id)
            matchViewModel.setCurrentSelectTeam(args.team!!.name!!)
        } else {
            matchViewModel.clearCurrentSelectTeam()
        }

        matchViewModel.getMatches()



        binding.swipeRefresh.setOnRefreshListener {
            matchViewModel.getMatches()
        }

    }

    override fun onWatchHighlightView(match: Match) {
        val destination =
            MatchesFragmentDirections.actionMatchesFragmentToVideoViewerFragment(match)
        findNavController().navigate(destination)
    }


    override fun onNotification(position: Int, match: Match) {

        matchViewModel.scheduleMatchNotification(requireContext(), match)
        upcomingAdapter.notifyItemChanged(position)
    }
}