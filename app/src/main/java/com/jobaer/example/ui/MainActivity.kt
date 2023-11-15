package com.jobaer.example.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import com.jobaer.example.R
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.jobaer.example.databinding.ActivityMainBinding
import com.jobaer.example.ui.viewmodels.MatchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val matchViewModel: MatchViewModel by viewModels()
    private lateinit var binding : ActivityMainBinding
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { _: Boolean ->

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        initViews()

        matchViewModel.scheduleCleanupWork(this)
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)

        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.matchesFragment -> {
                    if (!binding.bottomNavigationView.isVisible) {
                        showHideBottomNav(true)
                    }
                    supportActionBar?.title = getString(R.string.all_matches)
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
                R.id.teamsFragment -> {
                    if (!binding.bottomNavigationView.isVisible) {
                        showHideBottomNav(true)
                    }
                    supportActionBar?.title = getString(R.string.all_teams)
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
                R.id.videoViewerFragment -> {
                    showHideBottomNav(false)
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            navHostFragment.navController.popBackStack()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            matchViewModel.askNotificationPermission(this, requestPermissionLauncher)
        }
    }

    //Show animation for hiding/showing Bottom Nav
    fun showHideBottomNav(show:Boolean) {
        val animation = AnimationUtils.loadAnimation(this, if (show) R.anim.slide_up else R.anim.slide_down)
        binding.bottomNavigationView.startAnimation(animation)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                binding.bottomNavigationView.visibility = if (show) View.VISIBLE else View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }


}