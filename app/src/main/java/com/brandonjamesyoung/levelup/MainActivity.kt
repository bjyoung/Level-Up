package com.brandonjamesyoung.levelup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.brandonjamesyoung.levelup.databinding.ActivityMainBinding
import com.brandonjamesyoung.levelup.viewmodels.QuestListViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: QuestListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setupViewModels()
        setContentView(view)
    }

    private fun setupViewModels() {
        viewModel.questList.observe(this, Observer { quests ->
            // Do nothing, since fragments handle displaying quest data
        })
    }
}