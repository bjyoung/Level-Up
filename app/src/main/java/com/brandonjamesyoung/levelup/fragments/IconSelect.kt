package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.util.Log.i
import android.view.View
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
import com.brandonjamesyoung.levelup.data.Icon
import com.brandonjamesyoung.levelup.shared.*
import com.brandonjamesyoung.levelup.viewmodels.IconSelectViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IconSelect : Fragment(R.layout.icon_select) {
    private val viewModel: IconSelectViewModel by activityViewModels()

    private val iconGroupAdapterMap = mutableMapOf<IconGroup, IconGridAdapter>()

    private fun setEditMode() {
        viewModel.mode.value = Mode.EDIT
    }

    private fun activateEditModeButton() {
        ButtonHelper.convertButton(
            targetId = R.id.EditButton,
            iconDrawableId = R.drawable.white_arrow_icon,
            iconColorId = R.color.icon_primary,
            buttonMethod = ::setEditMode,
            view = requireView(),
            resources = resources
        )
    }

    private fun activateBackButton() {
        ButtonHelper.convertButton(
            targetId = R.id.BackButton,
            iconDrawableId = R.drawable.left_arrow_icon,
            buttonMethod = ::navigateToNewQuest,
            view = requireView(),
            resources = resources
        )
    }

    private fun navigateToNewIcon() {
        NavHostFragment.findNavController(this).navigate(R.id.action_iconSelect_to_newIcon)
        i(TAG, "Going from Icon Select to New Icon")
    }

    private fun activateAddIconButton() {
        ButtonHelper.convertButton(
            targetId = R.id.AddNewIconButton,
            iconDrawableId = R.drawable.plus_icon,
            buttonMethod = ::navigateToNewIcon,
            view = requireView(),
            resources = resources
        )
    }

    private fun activateDefaultMode() {
        activateEditModeButton()
        activateBackButton()
        activateAddIconButton()
    }

    private fun setDefaultMode() {
        viewModel.mode.value = Mode.DEFAULT
    }

    private fun activateSelectModeButton() {
        ButtonHelper.convertButton(
            targetId = R.id.EditButton,
            iconDrawableId = R.drawable.pencil_icon,
            iconColorId = R.color.icon_primary,
            buttonMethod = ::setDefaultMode,
            view = requireView(),
            resources = resources
        )
    }

    private fun deleteIcons() {
        viewModel.showSnackbar("Not implemented yet")
    }

    private fun activateDeleteButton() {
        ButtonHelper.convertButton(
            targetId = R.id.BackButton,
            iconDrawableId = R.drawable.pencil_icon,
            buttonMethod = ::deleteIcons,
            view = requireView(),
            resources = resources
        )
    }

    private fun moveIcons() {
        viewModel.showSnackbar("Not implemented yet")
    }

    private fun activateMoveIconsButton() {
        ButtonHelper.convertButton(
            targetId = R.id.AddNewIconButton,
            iconDrawableId = R.drawable.dash_icon,
            buttonMethod = ::moveIcons,
            view = requireView(),
            resources = resources
        )
    }

    // TODO Update New Quest method below to work for Icon Select
    private fun activateEditMode() {
        activateSelectModeButton()
        activateDeleteButton()
        activateMoveIconsButton()
    }

    private fun setupMode() {
        viewModel.mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> activateDefaultMode()
                Mode.EDIT -> activateEditMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }
    }

    private fun navigateToNewQuest() {
        NavHostFragment.findNavController(this).navigate(R.id.action_iconSelect_to_newQuest)
        i(TAG, "Going from Icon Select to Quest List")
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

    private fun addToIconGroupMap(iconGroup: IconGroup, iconList: List<Icon>) {
        val sortedIcons = iconList.sortedBy { icon -> icon.name }
        iconGroupAdapterMap[iconGroup] = IconGridAdapter(sortedIcons)
    }

    private suspend fun waitForIconGroupData() {
        try {
            var numLoops = 0

            while (iconGroupAdapterMap[viewModel.initSelectedGroup] == null) {
                numLoops += 1
                if (numLoops > MAX_NUM_LOOPS) throw CancellationException()
                delay(1L)
            }
        } catch (ex: CancellationException) {
            Log.e(TAG, "Initial icon group data loading timed out")
        }
    }

    private suspend fun setupObservables() {
        viewModel.spadesIcons.observe(viewLifecycleOwner) { spadesIcons ->
            addToIconGroupMap(IconGroup.SPADES, spadesIcons)
        }

        viewModel.diamondsIcons.observe(viewLifecycleOwner) { diamondsIcons ->
            addToIconGroupMap(IconGroup.DIAMONDS, diamondsIcons)
        }

        viewModel.heartsIcons.observe(viewLifecycleOwner) { heartsIcons ->
            addToIconGroupMap(IconGroup.HEARTS, heartsIcons)
        }

        viewModel.clubsIcons.observe(viewLifecycleOwner) { clubsIcons ->
            addToIconGroupMap(IconGroup.CLUBS, clubsIcons)
        }

        waitForIconGroupData()

        viewModel.selectedIconGroup.observe(viewLifecycleOwner) { selectedIconGroup ->
            switchIconGroup(selectedIconGroup)
        }

        // TODO Can make base fragment observe view model message to reduce repeat code
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
            setupMode()
            setupIconGrid()
            setupIconGroupButtons()
            setupObservables()
            viewModel.selectedIconGroup.value = viewModel.initSelectedGroup
        }
    }

    companion object {
        private const val TAG = "Icon Select"
    }
}