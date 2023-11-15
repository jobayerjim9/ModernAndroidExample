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
import com.jobaer.example.R
import com.jobaer.example.databinding.FragmentTeamsBinding
import com.jobaer.example.models.Team
import com.jobaer.example.ui.adapters.TeamClickHandler
import com.jobaer.example.ui.adapters.TeamsRvAdapter
import com.jobaer.example.ui.viewmodels.TeamViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamsFragment : Fragment(), TeamClickHandler {
    private lateinit var binding: FragmentTeamsBinding
    private val teamViewModel: TeamViewModel by viewModels()
    private lateinit var teamsRvAdapter: TeamsRvAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_teams, container, false)
        initViews()
        setupObservers()
        return binding.root
    }

    private fun initViews() {
        teamsRvAdapter = TeamsRvAdapter(this)
        binding.teamsRecycler.adapter = teamsRvAdapter
        teamViewModel.getTeams()
        binding.swipeRefresh.setOnRefreshListener {
            teamViewModel.getTeams()
        }

    }

    private fun setupObservers() {
        teamViewModel.teams.observe(viewLifecycleOwner) {
            if (it != null) {
                teamsRvAdapter.submitList(it)
            }
        }
        teamViewModel.message.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                teamViewModel._message.postValue("")
            }
        }
        teamViewModel.loader.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.swipeRefresh.isRefreshing = it
            }
        }

    }

    override fun onClickTeam(team: Team) {
        val destination = TeamsFragmentDirections.actionTeamsFragmentToMatchesFragment(team)
        findNavController().navigate(destination)
    }

}