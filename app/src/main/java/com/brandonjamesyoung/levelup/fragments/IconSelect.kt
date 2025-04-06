package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brandonjamesyoung.levelup.constants.IconGroup
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Icon
import com.brandonjamesyoung.levelup.data.SelectableIcon
import com.brandonjamesyoung.levelup.utility.*
import com.brandonjamesyoung.levelup.utility.OrientationManager.Companion.inPortraitMode
import com.brandonjamesyoung.levelup.utility.ScreenHelper.Companion.getScreenHeight
import com.brandonjamesyoung.levelup.utility.ScreenHelper.Companion.getScreenWidth
import com.brandonjamesyoung.levelup.utility.SnackbarHelper.Companion.showSnackbar
import com.brandonjamesyoung.levelup.viewmodels.IconSelectViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IconSelect : Fragment(R.layout.icon_select) {
    private val viewModel: IconSelectViewModel by activityViewModels()

    private var latestIcons: MutableList<Icon> = mutableListOf()

    @Inject lateinit var buttonConverter: ButtonConverter

    @Inject lateinit var iconGridCreator: IconGridCreator

    private fun convertButton(
        targetId: Int,
        iconDrawableId: Int,
        iconColorId: Int = R.color.nav_button_icon,
        buttonMethod: () -> Unit,
        tooltip: String? = null
    ) {
        buttonConverter.convertNavButton(
            targetId = targetId,
            iconDrawableId = iconDrawableId,
            iconColorId = iconColorId,
            buttonMethod = buttonMethod,
            tooltip = tooltip,
            view = requireView()
        )
    }

    private fun changeTitle(newTitle: String) {
        val title = requireView().findViewById<TextView>(R.id.IconSelectLabel)
        title.text = newTitle
    }

    private fun activateEditModeButton() {
        convertButton(
            targetId = R.id.EditButton,
            iconDrawableId = R.drawable.arrow_pointer_icon_large,
            iconColorId = R.color.icon_primary,
            buttonMethod = { viewModel.switchMode(Mode.EDIT) },
            tooltip = getString(R.string.edit_icons_button_tooltip)
        )
    }

    private fun activateBackButton() {
        convertButton(
            targetId = R.id.BackButton,
            iconDrawableId = R.drawable.arrow_left_icon_large,
            buttonMethod = ::navigateToNewQuest,
        )
    }

    private fun navigateToNewIcon() {
        findNavController().navigate(R.id.action_iconSelect_to_newIcon)
        Log.i(TAG, "Going from Icon Select to New Icon")
    }

    private fun activateAddIconButton() {
        convertButton(
            targetId = R.id.AddNewIconButton,
            iconDrawableId = R.drawable.plus_icon_large,
            buttonMethod = ::navigateToNewIcon,
            tooltip = getString(R.string.add_icon_button_tooltip)
        )
    }

    private fun activateSelectMode() {
        Log.i(TAG, "In select mode")
        activateEditModeButton()
        activateBackButton()
        activateAddIconButton()
        viewModel.selectedIconIds.clear()
        setupIconGrid(latestIcons)
    }

    private fun activateSelectModeButton() {
        convertButton(
            targetId = R.id.EditButton,
            iconDrawableId = R.drawable.pencil_icon_large,
            iconColorId = R.color.icon_primary,
            buttonMethod = { viewModel.switchMode(Mode.SELECT) },
            tooltip = getString(R.string.select_mode_button_tooltip)
        )
    }

    private fun deleteIcons() {
        if (viewModel.selectedIconIds.isEmpty()) {
            val message = getString(R.string.no_icons_selected_message)
            viewModel.showSnackbar(message)
            return
        }

        val selectedIconIdsCopy: Set<Int> = viewModel.selectedIconIds.toSet()
        viewModel.deleteIcons(selectedIconIdsCopy)
        latestIcons.removeAll { it.id in selectedIconIdsCopy }
        viewModel.selectedIconIds.clear()
        setupIconGrid(latestIcons)
        viewModel.switchMode(Mode.EDIT)
    }

    private fun activateDeleteButton() {
        convertButton(
            targetId = R.id.BackButton,
            iconDrawableId = R.drawable.trash_bin_icon_large,
            iconColorId = R.color.warning_icon,
            buttonMethod = ::deleteIcons,
            tooltip = getString(R.string.delete_icons_button_tooltip)
        )
    }

    private fun setupMoveMode() {
        if (viewModel.selectedIconIds.isEmpty()) {
            val message = getString(R.string.no_icons_selected_message)
            viewModel.showSnackbar(message)
            return
        }

        viewModel.switchMode(Mode.MOVE)
    }

    private fun activateMoveButton() {
        convertButton(
            targetId = R.id.AddNewIconButton,
            iconDrawableId = R.drawable.dash_icon_large,
            buttonMethod = ::setupMoveMode,
            tooltip = getString(R.string.move_icons_button_tooltip)
        )
    }

    private fun enableButton(id: Int) {
        val view = requireView()
        val button = view.findViewById<MaterialButton>(id) ?: return
        button.alpha = 1.0F
        button.isEnabled = true
    }

    private fun enableNonMoveModeButtons() {
        enableButton(R.id.BackButton)
        enableButton(R.id.EditButton)
    }

    private fun activateEditMode() {
        Log.i(TAG, "In edit mode")
        changeTitle("Select an Icon")
        enableNonMoveModeButtons()
        activateSelectModeButton()
        activateDeleteButton()
        activateMoveButton()
        setupSwappableIconGroups()

        // To re-enable icon scrolling and icon buttons if needed
        setupIconGrid(latestIcons)
    }

    private fun disableButton(id: Int) {
        val view = requireView()
        val button = view.findViewById<MaterialButton>(id) ?: return
        button.alpha = DISABLE_TRANSPARENCY
        button.isEnabled = false
    }

    private fun disableNonMoveModeButtons() {
        disableButton(R.id.BackButton)
        disableButton(R.id.EditButton)
    }

    private fun moveIcons(iconGroup: IconGroup) {
        val selectedIconIdsCopy: Set<Int> = viewModel.selectedIconIds.toSet()
        viewModel.moveIcons(selectedIconIdsCopy, iconGroup)
        latestIcons.removeAll { it.id in selectedIconIdsCopy }
        viewModel.switchMode(Mode.EDIT)
    }

    // Calculate zoom-in deltas to translate to (center of screen, spread out horizontally)
    // Position must be between 1 and 4 inclusive
    private fun calculateZoomInDeltas(button: ImageButton, position: Int) : Pair<Float, Float> {
        val windowManager = requireActivity().windowManager
        val screenWidth: Float = getScreenWidth(windowManager).toFloat()
        val screenHeight: Float = getScreenHeight(windowManager).toFloat()
        val buttonCenterX: Float = button.width.toFloat() / 2F
        val buttonCenterY: Float = button.height.toFloat() / 2F
        val interButtonSpacing: Float = (screenWidth - ZOOM_IN_MARGIN * 2F) / 4F
        var toX: Float = ZOOM_IN_MARGIN + interButtonSpacing * (position - 1) + buttonCenterX
        if (!inPortraitMode(resources)) toX += 200 // TODO fix this method so it doesn't rely on magic number to center icons
        val toY: Float = screenHeight * 0.5F - buttonCenterY
        val fromX: Float = button.x - button.translationX
        val fromY: Float = button.y - button.translationY
        return Pair(toX - fromX, toY - fromY)
    }

    // Position must be between 1 and 4 inclusive
    private fun animateZoomIn(button: ImageButton, position: Int) {
        val (deltaX, deltaY) = calculateZoomInDeltas(button, position)

        val animator: ViewPropertyAnimator = button.animate()
            .scaleX(ZOOM_IN_SCALE)
            .scaleY(ZOOM_IN_SCALE)
            .translationX(deltaX)
            .translationY(deltaY)
            .setInterpolator(AccelerateDecelerateInterpolator())

        animator.duration = ZOOM_DURATION
        animator.start()
    }

    // Move icon group buttons to center of screen and change to move icons on press
    private fun setupIconGroupSelect() {
        val iconGroupIdAnimMap = mapOf(
            R.id.SpadesGroupButton to IconGroup.SPADES,
            R.id.DiamondsGroupButton to IconGroup.DIAMONDS,
            R.id.HeartsGroupButton to IconGroup.HEARTS,
            R.id.ClubsGroupButton to IconGroup.CLUBS
        )

        val view = requireView()
        var iconGroupPosition = 0

        for (buttonId: Int in iconGroupIdAnimMap.keys) {
            iconGroupPosition += 1
            val imageButton = view.findViewById<ImageButton>(buttonId)
            animateZoomIn(imageButton, iconGroupPosition)
            val iconGroup = iconGroupIdAnimMap[buttonId] ?: continue

            if (iconGroup == viewModel.selectedIconGroup.value) {
                imageButton.setOnClickListener{
                    val message = getString(R.string.select_another_icon_group_message)
                    viewModel.showSnackbar(message)
                }
            } else {
                imageButton.setOnClickListener{
                    moveIcons(iconGroup)
                }
            }
        }
    }

    private fun cancelMove() {
        viewModel.switchMode(Mode.EDIT)
    }

    private fun activateCancelButton() {
        convertButton(
            targetId = R.id.AddNewIconButton,
            iconDrawableId = R.drawable.cancel_icon_large,
            buttonMethod = ::cancelMove
        )
    }

    private fun activateMoveMode() {
        Log.i(TAG, "In move mode")
        changeTitle("Select an Icon Group")
        disableNonMoveModeButtons()
        activateCancelButton()
        setupIconGroupSelect()

        // Reload icon grid to disable scrolling and icon buttons
        setupIconGrid(latestIcons)
    }

    private fun setupMode() {
        viewModel.switchMode(Mode.SELECT)

        viewModel.mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.SELECT -> activateSelectMode()
                Mode.EDIT -> activateEditMode()
                Mode.MOVE -> activateMoveMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }
    }

    private fun navigateToNewQuest() {
        findNavController().navigate(R.id.action_iconSelect_to_newQuest)
        Log.i(TAG, "Going from Icon Select to Quest List")
    }

    private fun navigateToNewQuest(icon: Icon) {
        val action = IconSelectDirections.actionIconSelectToNewQuest(iconId = icon.id)
        findNavController().navigate(action)
        Log.i(TAG, "Selected ${icon.name} icon. Going from Icon Select to New Quest.")
    }

    // Move icon groups back to their original position and sizes
    private fun animateZoomOut(button: ImageButton) {
        val animator: ViewPropertyAnimator = button.animate()
            .scaleX(1F)
            .scaleY(1F)
            .translationX(0F)
            .translationY(0F)
            .setInterpolator(AccelerateDecelerateInterpolator())

        animator.duration = ZOOM_DURATION
        animator.start()
    }

    private fun setupSwappableIconGroups() {
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

            // TODO check if the line below is necessary
            animateZoomOut(imageButton)
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

    private fun applyDefaultColor(iconGroup: IconGroup) {
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

    private fun applySelectedColor(iconGroup: IconGroup) {
        val buttonId = getIconGroupButtonId(iconGroup) ?: return
        val view = requireView()
        val button = view.findViewById<ImageButton>(buttonId)
        val theme = view.context.theme
        val selectedBackgroundColor = resources.getColor(R.color.background_secondary, theme)
        button.setBackgroundColor(selectedBackgroundColor)
        button.imageTintList = resources.getColorStateList(R.color.icon_primary, theme)
    }

    private fun highlightSelectedGroup(selectedIconGroup: IconGroup) {
        val currentIconGroup = viewModel.selectedIconGroup.value
        if (currentIconGroup != null) applyDefaultColor(currentIconGroup)
        applySelectedColor(selectedIconGroup)
    }

    private fun removeHighlightFromOtherGroups(iconGroupToSkip: IconGroup) {
        val iconGroups: MutableList<IconGroup> = mutableListOf(
            IconGroup.SPADES,
            IconGroup.DIAMONDS,
            IconGroup.HEARTS,
            IconGroup.CLUBS
        )

        iconGroups.remove(iconGroupToSkip)
        iconGroups.forEach { applyDefaultColor(it) }
    }

    private fun setupIconGrid(icons: List<Icon>?) {
        if(icons == null) return
        if (icons.isEmpty()) showNoIconsMessage() else hideNoIconsMessage()
        latestIcons = icons.toMutableList()

        val selectableIcons: List<SelectableIcon> = icons.map { icon ->
            SelectableIcon(
                icon = icon,
                selected = icon.id in viewModel.selectedIconIds
            )
        }

        val composeView = requireView().findViewById<ComposeView>(R.id.IconSelectComposeView)

        composeView.setContent {
            iconGridCreator.IconSelectGridView(
                icons = selectableIcons,
                iconAction = ::selectIcon,
                pageMode = { viewModel.mode.value }
            )
        }

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

    private fun selectIcon(selectableIcon: SelectableIcon) {
        val icon = selectableIcon.icon

        // If an icon is selected while in default mode, return selected icon id back to New Quest
        if (viewModel.mode.value == Mode.SELECT) {
            navigateToNewQuest(icon)
            return
        }

        selectableIcon.selected = !selectableIcon.selected

        if (icon.id !in viewModel.selectedIconIds) {
            Log.i(TAG, "Select icon ${icon.name}")
            viewModel.selectedIconIds.add(icon.id)
        } else {
            Log.i(TAG, "De-select icon ${icon.name}")
            viewModel.selectedIconIds.remove(icon.id)
        }
    }

    // Change selected icon group and trigger icon grid reload
    private fun swapIconGroup(iconGroup: IconGroup?) {
        if (iconGroup == null) return
        Log.i(TAG, "Switch to ${iconGroup.name} icon group")
        viewModel.selectedIconIds.clear()
        highlightSelectedGroup(iconGroup)
        removeHighlightFromOtherGroups(iconGroup)
        viewModel.loadIconGroup(iconGroup)
    }

    private fun setupObservables() {
        viewModel.selectedIconGroup.observe(viewLifecycleOwner) { selectedIconGroup ->
            swapIconGroup(selectedIconGroup)
        }

        viewModel.displayedIcons.observe(viewLifecycleOwner) { icons ->
            setupIconGrid(icons)
        }

        // TODO Can make base fragment observe view model message to reduce repeat code
        viewModel.message.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                val view = requireView()
                val addNewIconButton: View = view.findViewById(R.id.AddNewIconButton)
                showSnackbar(it, view, addNewIconButton)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InsetHandler.addInsetPadding(requireView())

        lifecycleScope.launch {
            Log.i(TAG, "On Icon Select page")
            setupMode()
            setupSwappableIconGroups()
            setupObservables()
        }
    }

    companion object {
        private const val TAG = "Icon Select"
        private const val DISABLE_TRANSPARENCY = 0.25F
        private const val ZOOM_IN_SCALE = 1.5F
        private const val ZOOM_DURATION = 400L
        private const val ZOOM_IN_MARGIN = 75
    }
}