package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
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
import com.brandonjamesyoung.levelup.data.SelectedIcon
import com.brandonjamesyoung.levelup.shared.*
import com.brandonjamesyoung.levelup.shared.DimensionHelper.Companion.getScreenHeight
import com.brandonjamesyoung.levelup.shared.DimensionHelper.Companion.getScreenWidth
import com.brandonjamesyoung.levelup.shared.SnackbarHelper.Companion.showSnackbar
import com.brandonjamesyoung.levelup.viewmodels.IconSelectViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IconSelect : Fragment(R.layout.icon_select) {
    private val viewModel: IconSelectViewModel by activityViewModels()

    private val iconGroupAdapterMap = mutableMapOf<IconGroup, IconGridAdapter>()

    private val originalIconGroupPositionMap = mutableMapOf<IconGroup, Pair<Int, Int>>()

    private fun recordOriginalIconGroupPositions() {
        // TODO Get icon group button views

        // TODO Store their positions into the map
    }

    private fun clearSelectedIcons() {
        val currentIconGroupAdapter = iconGroupAdapterMap[viewModel.currentIconGroup]
        currentIconGroupAdapter?.clearSelectedIcons(this)
    }

    private fun convertButton(
        targetId: Int,
        iconDrawableId: Int,
        iconColorId: Int = R.color.nav_button_icon,
        buttonMethod: () -> Unit
    ) {
        ButtonHelper.convertButton(
            targetId = targetId,
            iconDrawableId = iconDrawableId,
            iconColorId = iconColorId,
            buttonMethod = buttonMethod,
            view = requireView(),
            resources = resources
        )
    }

    private fun changeTitle(newTitle: String) {
        val title = requireView().findViewById<TextView>(R.id.IconSelectLabel)
        title.text = newTitle
    }

    private fun activateEditModeButton() {
        val setEditMode = { viewModel.mode.value = Mode.EDIT }

        convertButton(
            targetId = R.id.EditButton,
            iconDrawableId = R.drawable.white_arrow_icon,
            iconColorId = R.color.icon_primary,
            buttonMethod = setEditMode,
        )
    }

    private fun activateBackButton() {
        convertButton(
            targetId = R.id.BackButton,
            iconDrawableId = R.drawable.left_arrow_icon,
            buttonMethod = ::navigateToNewQuest,
        )
    }

    private fun navigateToNewIcon() {
        NavHostFragment.findNavController(this).navigate(R.id.action_iconSelect_to_newIcon)
        Log.i(TAG, "Going from Icon Select to New Icon")
    }

    private fun activateAddIconButton() {
        convertButton(
            targetId = R.id.AddNewIconButton,
            iconDrawableId = R.drawable.plus_icon,
            buttonMethod = ::navigateToNewIcon,
        )
    }

    private fun activateDefaultMode() {
        Log.i(TAG, "In default mode")
        activateEditModeButton()
        activateBackButton()
        activateAddIconButton()
        clearSelectedIcons()
    }

    private fun setDefaultMode() {
        viewModel.mode.value = Mode.DEFAULT
    }

    private fun activateSelectModeButton() {
        convertButton(
            targetId = R.id.EditButton,
            iconDrawableId = R.drawable.pencil_icon,
            iconColorId = R.color.icon_primary,
            buttonMethod = ::setDefaultMode,
        )
    }

    private fun deleteIcons() {
        viewModel.showSnackbar("Not implemented yet")
    }

    private fun activateDeleteButton() {
        convertButton(
            targetId = R.id.BackButton,
            iconDrawableId = R.drawable.trash_bin_icon,
            iconColorId = R.color.delete,
            buttonMethod = ::deleteIcons,
        )
    }

    private fun areIconsSelected() : Boolean {
        val currentAdapter = iconGroupAdapterMap[viewModel.currentIconGroup]

        if (currentAdapter == null) {
            Log.e(TAG, "No RecyclerView adapter found")
            return false
        }

        return currentAdapter.getSelectedIcons().isNotEmpty()
    }

    private fun setMoveMode() {
        if (!areIconsSelected()) {
            val message = getString(R.string.no_icons_selected_message)
            viewModel.showSnackbar(message)
            return
        }

        viewModel.mode.value = Mode.MOVE
    }

    private fun activateMoveButton() {
        convertButton(
            targetId = R.id.AddNewIconButton,
            iconDrawableId = R.drawable.dash_icon,
            buttonMethod = ::setMoveMode,
        )
    }

    private fun enableIconButtons() {
        val currentAdapter = iconGroupAdapterMap[viewModel.currentIconGroup] ?: return
        val view = requireView()
        val iconGrid: RecyclerView = view.findViewById(R.id.IconGrid)
        val layoutManager = iconGrid.layoutManager ?: return

        for (i in 0 until currentAdapter.itemCount) {
            val layoutChild: View = layoutManager.findViewByPosition(i) ?: continue
            val iconButton: FloatingActionButton = layoutChild.findViewById(R.id.QuestIcon)
            iconButton.alpha = 1.0F
            iconButton.isEnabled = true
            val iconText: TextView = layoutChild.findViewById(R.id.IconName)
            iconText.alpha = 1.0F
        }
    }

    private fun enableIconGridScrolling() {
        val view = requireView()
        val iconGrid: RecyclerView = view.findViewById(R.id.IconGrid)
        iconGrid.suppressLayout(false)
    }

    private fun enableButton(id: Int) {
        val view = requireView()
        val button = view.findViewById<MaterialButton>(id) ?: return
        button.alpha = 1.0F
        button.isEnabled = true
    }

    private fun enableNonMoveModeButtons() {
        enableIconButtons()
        enableIconGridScrolling()
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
    }

    private fun disableIconButtons() {
        val currentAdapter = iconGroupAdapterMap[viewModel.currentIconGroup] ?: return
        val view = requireView()
        val iconGrid: RecyclerView = view.findViewById(R.id.IconGrid)
        val layoutManager = iconGrid.layoutManager ?: return

        for (i in 0 until currentAdapter.itemCount) {
            val layoutChild: View = layoutManager.findViewByPosition(i) ?: continue
            val iconButton: FloatingActionButton = layoutChild.findViewById(R.id.QuestIcon)
            iconButton.alpha = DISABLE_TRANSPARENCY
            iconButton.isEnabled = false
            val iconText: TextView = layoutChild.findViewById(R.id.IconName)
            iconText.alpha = DISABLE_TRANSPARENCY
        }
    }

    private fun disableIconGridScrolling() {
        val view = requireView()
        val iconGrid: RecyclerView = view.findViewById(R.id.IconGrid)
        iconGrid.suppressLayout(true)
    }

    private fun disableButton(id: Int) {
        val view = requireView()
        val button = view.findViewById<MaterialButton>(id) ?: return
        button.alpha = DISABLE_TRANSPARENCY
        button.isEnabled = false
    }

    private fun disableNonMoveModeButtons() {
        disableIconButtons()
        disableIconGridScrolling()
        disableButton(R.id.BackButton)
        disableButton(R.id.EditButton)
    }

    private fun moveIcons(iconGroup: IconGroup) {
        val currentAdapter = iconGroupAdapterMap[viewModel.currentIconGroup]

        if (currentAdapter == null) {
            viewModel.mode.value = Mode.EDIT
            Log.e(TAG, "No RecyclerView adapter found")
            return
        }

        val selectedIcons: List<SelectedIcon> = currentAdapter.getSelectedIcons()
        val selectedIconIds: List<Int> = selectedIcons.map { icon -> icon.id }
        viewModel.moveIcons(selectedIconIds, iconGroup)
        currentAdapter.clearSelectedIcons(this)
        viewModel.mode.value = Mode.EDIT
    }

    // Calculate zoom-in deltas to translate to
    // Position must be between 1 and 4 inclusive
    private fun calculateZoomInDeltas(button: ImageButton, position: Int) : Pair<Float, Float> {
        val windowManager = requireActivity().windowManager
        val screenWidth: Float = getScreenWidth(windowManager).toFloat()
        val screenHeight: Float = getScreenHeight(windowManager).toFloat()
        val buttonCenterX: Float = button.width.toFloat() / 2F
        val buttonCenterY: Float = button.height.toFloat() / 2F
        val interButtonSpacing: Float = (screenWidth - ZOOM_IN_MARGIN * 2F) / 4F
        val toX: Float = ZOOM_IN_MARGIN + interButtonSpacing * (position - 1) + buttonCenterX
        val toY: Float = screenHeight * 0.5F - buttonCenterY
        val fromX: Float = button.x
        val fromY: Float = button.y
        return Pair(toX - fromX, toY - fromY)
    }

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

    private fun setupSelectableIconGroups() {
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

            if (iconGroup == viewModel.currentIconGroup) {
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
        viewModel.mode.value = Mode.EDIT
    }

    private fun activateCancelButton() {
        convertButton(
            targetId = R.id.AddNewIconButton,
            iconDrawableId = R.drawable.cancel_icon,
            buttonMethod = ::cancelMove,
        )
    }

    private fun activateMoveMode() {
        Log.i(TAG, "In move mode")
        disableNonMoveModeButtons()
        setupSelectableIconGroups()
        activateCancelButton()
        changeTitle("Select an Icon Group")
    }

    private fun setupMode() {
        viewModel.mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> activateDefaultMode()
                Mode.EDIT -> activateEditMode()
                Mode.MOVE -> activateMoveMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }
    }

    private fun navigateToNewQuest() {
        NavHostFragment.findNavController(this).navigate(R.id.action_iconSelect_to_newQuest)
        Log.i(TAG, "Going from Icon Select to Quest List")
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
        val currentIconGroup = viewModel.currentIconGroup
        if (currentIconGroup != null) useDefaultColor(currentIconGroup)
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

        Log.d(TAG, "Icon ids in ${iconGroup.name}: " + newAdapter.getIconIds().contentToString())
        if (newAdapter.itemCount == 0) showNoIconsMessage() else hideNoIconsMessage()

        when (iconGrid.adapter) {
            null -> iconGrid.adapter = newAdapter
            newAdapter -> return
            else -> iconGrid.swapAdapter(newAdapter, false)
        }
    }

    private fun switchIconGroup(iconGroup: IconGroup?) {
        if (iconGroup == null) return
        clearSelectedIcons()
        highlightSelectedGroup(iconGroup)
        loadIcons(iconGroup)
        viewModel.currentIconGroup = iconGroup
        Log.i(TAG, "Switch to ${iconGroup.name} icon group")
    }

    private fun addToIconGroupMap(iconGroup: IconGroup, iconList: List<Icon>) {
        val sortedIcons = iconList.sortedBy { icon -> icon.name }
        val newAdapter = IconGridAdapter(sortedIcons)

        newAdapter.setGetModeCallback {
            viewModel.mode.value
        }

        iconGroupAdapterMap[iconGroup] = newAdapter
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

        viewModel.selectedIconGroup.value = viewModel.initSelectedGroup

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

        lifecycleScope.launch {
            Log.i(TAG, "On Icon Select page")
            setupMode()
            setupIconGrid()
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