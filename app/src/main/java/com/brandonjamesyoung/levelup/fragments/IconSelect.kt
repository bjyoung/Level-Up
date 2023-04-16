package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.util.Log.i
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.adapters.IconGridAdapter
import com.brandonjamesyoung.levelup.shared.IconGroup
import com.brandonjamesyoung.levelup.shared.SnackbarHelper
import com.brandonjamesyoung.levelup.viewmodels.IconSelectViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IconSelect : Fragment(R.layout.icon_select) {
    private val viewModel: IconSelectViewModel by activityViewModels()

    private val iconGroupAdapterMap = mutableMapOf<IconGroup, IconGridAdapter>()

    private fun navigateToQuestList() {
        NavHostFragment.findNavController(this).navigate(R.id.action_iconSelect_to_newQuest)
        i(TAG, "Going from Icon Select to Quest List")
    }

    private fun setupNavButtons() {
        setupBackButton()
        setupNewIconButton()
    }

    private fun setupNewIconButton() {
        val view = requireView()
        val addNewIconButton = view.findViewById<Button>(R.id.AddNewIconButton)

        addNewIconButton.setOnClickListener{
            viewModel.showSnackbar("Not implemented yet")
        }
    }

    private fun setupBackButton() {
        val view = requireView()
        val button = view.findViewById<View>(R.id.BackButton)

        button.setOnClickListener{
            navigateToQuestList()
        }
    }

    private fun setupEditButton() {
        val view = requireView()
        val editButton = view.findViewById<Button>(R.id.EditButton)

        editButton.setOnClickListener{
            viewModel.showSnackbar("Not implemented yet")
        }
    }

    private fun setupIconGroupButtons() {
        val iconGroupMap = mapOf(
            R.id.SpadesGroupButton to IconGroup.SPADES,
            R.id.DiamondsGroupButton to IconGroup.DIAMONDS,
            R.id.HeartsGroupButton to IconGroup.HEARTS,
            R.id.ClubsGroupButton to IconGroup.CLUBS
        )

        val view = requireView()

        for (buttonId: Int in iconGroupMap.keys) {
            val imageButton = view.findViewById<ImageButton>(buttonId)

            imageButton.setOnClickListener{
                iconGroupMap[buttonId]?.let { it1 -> viewModel.selectedIconGroup.value = it1 }
            }
        }
    }

    private fun getIconGroupButtonId(iconGroup: IconGroup) : Int? {
        val iconGroupMap = mapOf(
            IconGroup.SPADES to R.id.SpadesGroupButton,
            IconGroup.DIAMONDS to R.id.DiamondsGroupButton,
            IconGroup.HEARTS to R.id.HeartsGroupButton,
            IconGroup.CLUBS to R.id.ClubsGroupButton
        )

        val buttonId = iconGroupMap[iconGroup]

        if (buttonId == null) {
            Log.e(TAG, "Icon group's corresponding button id not found")
            return null
        }

        return buttonId
    }

    private fun useDefaultColor(iconGroup: IconGroup) {
        val buttonId = getIconGroupButtonId(iconGroup) ?: return
        val view = requireView()
        val button = view.findViewById<ImageButton>(buttonId)
        val theme = view.context.theme

        val defaultBackground = ResourcesCompat.getDrawable(
            resources,
            R.drawable.white_border_thin,
            theme
        )

        button.background = defaultBackground
        button.imageTintList = resources.getColorStateList(R.color.icon_secondary, theme)
    }

    private fun useSelectedColor(iconGroup: IconGroup) {
        val buttonId = getIconGroupButtonId(iconGroup) ?: return
        val view = requireView()
        val button = view.findViewById<ImageButton>(buttonId)
        val theme = view.context.theme
        val selectedBackgroundColor = resources.getColor(R.color.background_secondary, theme)
        button.setBackgroundColor(selectedBackgroundColor)
        button.imageTintList = resources.getColorStateList(R.color.icon_primary, theme)
    }

    private fun highlightSelectedGroup(selectedIconGroup: IconGroup) {
        val allIconGroups = arrayOf(
            IconGroup.SPADES,
            IconGroup.DIAMONDS,
            IconGroup.HEARTS,
            IconGroup.CLUBS
        )

        for (iconGroup in allIconGroups) {
            useDefaultColor(iconGroup)
        }

        useSelectedColor(selectedIconGroup)
    }

    private fun setupButtons() {
        setupNavButtons()
        setupEditButton()
        setupIconGroupButtons()
    }

    private fun setupIconGrid() {
        val view = requireView()
        val iconGrid: RecyclerView = view.findViewById(R.id.IconGrid)

        val horizontalGridLayoutManager = GridLayoutManager(
            view.context,
            resources.getInteger(R.integer.grid_rows),
            GridLayoutManager.HORIZONTAL,
            false
        )

        iconGrid.layoutManager = horizontalGridLayoutManager
    }

    private fun showNoIconsMessage() {
        val view = requireView()
        val noIconsMessage = view.findViewById<TextView>(R.id.NoIconsMessage)
        noIconsMessage.visibility = View.VISIBLE
    }

    private fun hideNoIconsMessage() {
        val view = requireView()
        val noIconsMessage = view.findViewById<TextView>(R.id.NoIconsMessage)
        noIconsMessage.visibility = View.GONE
    }

    private fun loadIcons(iconGroup: IconGroup) {
        val view = requireView()
        val iconGrid: RecyclerView = view.findViewById(R.id.IconGrid)
        val newAdapter: IconGridAdapter? = iconGroupAdapterMap[iconGroup]

        if (newAdapter == null) {
            Log.e(TAG, "No adapter found for iconGroup: $iconGroup")
            return
        }

        if (newAdapter.itemCount == 0) showNoIconsMessage() else hideNoIconsMessage()

        when (iconGrid.adapter) {
            null -> iconGrid.adapter = newAdapter
            newAdapter -> return
            else -> iconGrid.swapAdapter(newAdapter, false)
        }
    }

    private fun switchIconGroup(iconGroup: IconGroup?) {
        if (iconGroup == null) return
        highlightSelectedGroup(iconGroup)
        loadIcons(iconGroup)
    }

    private suspend fun setupObservables() {
        viewModel.spadesIcons.observe(viewLifecycleOwner) { spadesIcons ->
            iconGroupAdapterMap[IconGroup.SPADES] = IconGridAdapter(spadesIcons)
        }

        viewModel.diamondsIcons.observe(viewLifecycleOwner) { diamondsIcons ->
            iconGroupAdapterMap[IconGroup.DIAMONDS] = IconGridAdapter(diamondsIcons)
        }

        viewModel.heartsIcons.observe(viewLifecycleOwner) { heartsIcons ->
            iconGroupAdapterMap[IconGroup.HEARTS] = IconGridAdapter(heartsIcons)
        }

        viewModel.clubsIcons.observe(viewLifecycleOwner) { clubsIcons ->
            iconGroupAdapterMap[IconGroup.CLUBS] = IconGridAdapter(clubsIcons)
        }

        // TODO wrap below in a time limit in case the group is never set
        while (iconGroupAdapterMap[viewModel.initSelectedGroup] == null) {
            delay(1L)
        }

        viewModel.selectedIconGroup.observe(viewLifecycleOwner) { selectedIconGroup ->
            switchIconGroup(selectedIconGroup)
        }

        // TODO Can make base fragment observe viewmodel message to reduce repeat code
        viewModel.message.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                val view = requireView()
                val addNewIconButton: View = view.findViewById(R.id.AddNewIconButton)
                SnackbarHelper.showSnackbar(it, view, addNewIconButton)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            i(TAG, "On Icon Select page")
            setupIconGrid()
            setupButtons()
            setupObservables()
            viewModel.selectedIconGroup.value = viewModel.initSelectedGroup
        }
    }

    companion object {
        private const val TAG = "Icon Select"
    }
}