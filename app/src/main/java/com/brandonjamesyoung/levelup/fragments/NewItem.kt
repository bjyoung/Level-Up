package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Item
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.validation.Validation
import com.brandonjamesyoung.levelup.viewmodels.NewItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.Instant

@AndroidEntryPoint
class NewItem : Fragment(R.layout.new_item) {
    private val viewModel: NewItemViewModel by activityViewModels()

    private fun updateAcronym(settings: Settings?) {
        if (settings == null) {
            Log.e(TAG, "No settings to load")
            return
        }

        val view = requireView()
        val acronymLabel = view.findViewById<TextView>(R.id.CostRtLabel)
        acronymLabel.text = settings.pointsAcronym
    }

    private fun navigateToShop() {
        NavHostFragment.findNavController(this).navigate(R.id.action_newItem_to_shop)
        Log.i(TAG, "Going from New Item to Shop")
    }

    private fun addNavigation() {
        val view = requireView()
        val button = view.findViewById<View>(R.id.CancelButton)

        button.setOnClickListener{
            navigateToShop()
        }
    }

    private fun validateInput() : Boolean {
        val view = requireView()
        val nameView = view.findViewById<EditText>(R.id.ItemNameInput)
        if (!Validation.validateName(nameView, TAG, this)) return false
        val costView = view.findViewById<EditText>(R.id.CostInput)

        if (!Validation.validateNumField(costView, 0, 99999, this)) {
            return false
        }

        return true
    }

    private fun createItem() {
        val view = requireView()
        val nameInput = view.findViewById<EditText>(R.id.ItemNameInput)
        var name: String? = nameInput.text.trim().toString()

        if (name == "") {
            name = null
        }

        val costInput = view.findViewById<EditText>(R.id.CostInput)
        val cost = costInput.text.toString().toInt()

        val item = Item(
            name = name,
            cost = cost,
            dateCreated = Instant.now()
        )

        viewModel.insert(item)
    }

    private fun setupConfirmButton() {
        val view = requireView()
        val saveButton = view.findViewById<AppCompatButton>(R.id.ConfirmButton)

        saveButton.setOnClickListener {
            if (validateInput()){
                createItem()
                navigateToShop()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            Log.i(TAG, "On New Item page")
            addNavigation()
            setupConfirmButton()

            viewModel.settings.observe(viewLifecycleOwner) { settings ->
                updateAcronym(settings)
            }
        }
    }

    companion object {
        private const val TAG = "NewItem"
    }
}