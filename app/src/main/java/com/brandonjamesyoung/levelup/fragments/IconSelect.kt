package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log.i
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.adapters.IconGridAdapter
import com.brandonjamesyoung.levelup.data.Icon
import com.brandonjamesyoung.levelup.shared.SnackbarHelper
import com.brandonjamesyoung.levelup.viewmodels.IconSelectViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IconSelect : Fragment(R.layout.icon_select) {
    private val viewModel: IconSelectViewModel by activityViewModels()

    private fun navigateToQuestList() {
        NavHostFragment.findNavController(this).navigate(R.id.action_iconSelect_to_newQuest)
        i(TAG, "Going from Icon Select to Quest List")
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

    private fun setupNewIconButton() {
        val view = requireView()
        val addNewIconButton = view.findViewById<Button>(R.id.AddNewIconButton)

        addNewIconButton.setOnClickListener{
            viewModel.showSnackbar("Not implemented yet")
        }
    }

    private fun setupIconGroupButtons() {
        val buttonIds = arrayOf(
            R.id.SpadesGroupButton,
            R.id.DiamondsGroupButton,
            R.id.HeartsGroupButton,
            R.id.ClubsGroupButton
        )

        val view = requireView()

        for (buttonId: Int in buttonIds) {
            val imageButton = view.findViewById<ImageButton>(buttonId)

            imageButton.setOnClickListener{
                viewModel.showSnackbar("Not implemented yet")
            }
        }
    }

    private fun setupButtons() {
        setupBackButton()
        setupEditButton()
        setupNewIconButton()
        setupIconGroupButtons()
    }

    private fun setupIconGrid(iconList: List<Icon>) {
        val view = requireView()
        val iconGrid: RecyclerView = view.findViewById(R.id.IconGrid)
        iconGrid.adapter = IconGridAdapter(iconList)

        val horizontalGridLayoutManager = GridLayoutManager(
            view.context,
            resources.getInteger(R.integer.grid_rows),
            GridLayoutManager.HORIZONTAL,
            false
        )

        iconGrid.layoutManager = horizontalGridLayoutManager
    }

    private fun setupObservables() {
        viewModel.spadesIcons.observe(viewLifecycleOwner) { spadesIcons ->
            setupIconGrid(spadesIcons)
        }

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
            setupButtons()
            setupObservables()
        }
    }

    companion object {
        private const val TAG = "Icon Select"
    }
}