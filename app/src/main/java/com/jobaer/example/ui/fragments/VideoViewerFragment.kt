package com.jobaer.example.ui.fragments

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.navArgs
import com.jobaer.example.R
import com.jobaer.example.databinding.FragmentVideoViewerBinding
import com.jobaer.example.ui.MainActivity
import com.jobaer.example.utils.Utils


class VideoViewerFragment : Fragment() {
    private lateinit var binding: FragmentVideoViewerBinding
    private val args: VideoViewerFragmentArgs by navArgs()
    private var player: ExoPlayer? = null
    private var playbackPosition: Long = 0
    private var currentWindow: Int = 0
    private var playWhenReady: Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_video_viewer, container, false)
        playbackPosition = savedInstanceState?.getLong("playbackPosition") ?: 0
        currentWindow = savedInstanceState?.getInt("currentWindow") ?: 0
        playWhenReady = savedInstanceState?.getBoolean("playWhenReady") ?: true

        initializePlayer()
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.loading = true
        when (requireActivity()) {
            is MainActivity -> {
                (requireActivity() as MainActivity).supportActionBar?.title =
                    args.match.description
            }
        }
        val orientation = Utils.getScreenOrientation(requireContext())

        // can check if it's portrait or landscape.
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Handle landscape orientation
            when (requireActivity()) {
                is MainActivity -> {
                    (requireActivity() as MainActivity).supportActionBar?.hide()
                }
            }
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            binding.playerView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(Uri.parse(args.match.highlights))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = playWhenReady
            //To handle orientation change
            exoPlayer.seekTo(currentWindow, playbackPosition)

            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    binding.loading = state == Player.STATE_BUFFERING
                }
            })
        }
    }

    override fun onPause() {
        super.onPause()
        player?.let { player ->
            playbackPosition = player.currentPosition
            currentWindow = player.currentMediaItemIndex
            playWhenReady = player.playWhenReady
            player.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        when (requireActivity()) {
            is MainActivity -> {

                (requireActivity() as MainActivity).supportActionBar?.show()
            }
        }
        releasePlayer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("playbackPosition", playbackPosition)
        outState.putInt("currentWindow", currentWindow)
        outState.putBoolean("playWhenReady", playWhenReady)
    }

    private fun releasePlayer() {
        player?.run {
            release()
            player = null
        }
    }
}