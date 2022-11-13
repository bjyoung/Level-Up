package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Player
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.shared.Mode
import com.brandonjamesyoung.levelup.viewmodels.ShopViewModel
import kotlinx.coroutines.launch

private const val TAG = "Shop"

class Shop : Fragment(R.layout.shop) {
    private val shopViewModel: ShopViewModel by activityViewModels()
    private var mode: MutableLiveData<Mode> = MutableLiveData<Mode>()

    private fun addNavigation(view: View) {
        val buttonNavMap = mapOf(
//            R.id.AddNewItemButton to
//                    Pair(R.id.action_shop_to_newItem, "Going from Shop to New Item"),
            R.id.QuestListButton to
                    Pair(R.id.action_shop_to_questList, "Going from Shop to Quest List"),
            R.id.ShopSettingsButton to
                    Pair(R.id.action_shop_to_settings, "Going from Shop to Settings"),
        )

        for ((buttonId, navIdPair) in buttonNavMap) {
            val button = view.findViewById<View>(buttonId)
            val navId = navIdPair.first
            val logMessage = navIdPair.second

            button.setOnClickListener{
                NavHostFragment.findNavController(this).navigate(navId)
                Log.i(TAG, logMessage)
            }
        }
    }

    private fun activateDefaultMode() {
        Log.i(TAG, "Not implemented yet")
    }

    private fun activateSelectMode() {
        Log.i(TAG, "Not implemented yet")
    }

    private fun updatePoints(view: View, player: Player?) {
        val placeholderText = getString(R.string.placeholder_text)
        val rtStr = player?.rt?.toString() ?: placeholderText
        val pointsAmount = view.findViewById<TextView>(R.id.ShopPointsAmount)
        pointsAmount.text = rtStr
    }

    private fun updatePointsAcronym(settings: Settings?) {
        if (settings == null) return
        val view = requireView()
        val pointsLabel : TextView = view.findViewById(R.id.ShopPointsLabel)
        pointsLabel.text = settings.pointsAcronym
    }

    private fun setupObservables(view: View) {
        mode.value = Mode.DEFAULT

        mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> activateDefaultMode()
                Mode.SELECT -> activateSelectMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }

        shopViewModel.player.observe(viewLifecycleOwner) { player ->
            updatePoints(view, player)
        }

        shopViewModel.settings.observe(viewLifecycleOwner) { settings ->
            updatePointsAcronym(settings)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch{
            Log.i(TAG, "On Quest List page")
            addNavigation(view)
            setupObservables(view)
        }
    }
}