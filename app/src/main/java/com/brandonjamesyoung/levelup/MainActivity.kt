package com.brandonjamesyoung.levelup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.data.SettingsRepository
import com.brandonjamesyoung.levelup.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @Inject lateinit var settingsRepository: SettingsRepository

    private fun getSettings() = lifecycleScope.async(Dispatchers.IO){
        settingsRepository.get()
    }

    private suspend fun playerNameEntered(): Boolean {
        val settings: Settings? = getSettings().await()
        return settings == null || !settings.nameEntered
    }

    private fun changeStartingFragment(navId: Int) {
        val fragment = supportFragmentManager.findFragmentById(R.id.navHostFragment)
        val navHostFragment = fragment as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        graph.setStartDestination(navId)
        navHostFragment.navController.graph = graph
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        runBlocking {
            if (playerNameEntered()) changeStartingFragment(R.id.nameEntry)
        }
    }
}