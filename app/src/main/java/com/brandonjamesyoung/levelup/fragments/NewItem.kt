package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Item
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.validation.InputValidator
import com.brandonjamesyoung.levelup.viewmodels.NewItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NewItem : Fragment(R.layout.new_item) {
    private val viewModel: NewItemViewModel by activityViewModels()

    private var mode: MutableLiveData<Mode> = MutableLiveData<Mode>()

    private val args: NewItemArgs by navArgs()

    @Inject lateinit var validator: InputValidator

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
        if (!validator.validateQuestName(nameView, TAG, this)) return false
        val costView = view.findViewById<EditText>(R.id.CostInput)

        val costIsValid = validator.validateNumField(
            editText = costView,
            minNumber = -9999,
            maxNumber = 99999,
            fragment = this
        )

        if (!costIsValid) {
            return false
        }

        return true
    }

    private fun saveItem() {
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
        )

        if (mode.value == Mode.DEFAULT) {
            viewModel.insert(item)
        } else if (mode.value == Mode.EDIT) {
            item.id = args.itemId
            viewModel.update(item)
        }
    }

    private fun setupConfirmButton() {
        val view = requireView()
        val saveButton = view.findViewById<AppCompatButton>(R.id.ConfirmButton)

        saveButton.setOnClickListener {
            if (validateInput()){
                saveItem()
                navigateToShop()
            }
        }
    }

    private fun loadItem(item: Item) {
        val view = requireView()
        val nameInput = view.findViewById<EditText>(R.id.ItemNameInput)
        nameInput.setText(item.name)
        val costInput = view.findViewById<EditText>(R.id.CostInput)
        costInput.setText(item.cost.toString())
    }

    private fun activateEditMode() {
        val view = requireView()
        val pageLabel = view.findViewById<TextView>(R.id.NewItemLabel)
        pageLabel.text = getString(R.string.edit_item_label)

        viewModel.getItem(args.itemId).observe(viewLifecycleOwner) { item ->
            loadItem(item)
        }
    }

    private fun setupMode() {
        mode.value = Mode.DEFAULT

        if (args.itemId != INVALID_ITEM_ID) {
            mode.value = Mode.EDIT
        }

        mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> Unit
                Mode.EDIT -> activateEditMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            Log.i(TAG, "On New Item page")
            addNavigation()
            setupConfirmButton()
            setupMode()

            viewModel.settings.observe(viewLifecycleOwner) { settings ->
                updateAcronym(settings)
            }
        }
    }

    companion object {
        private const val TAG = "NewItem"
        private const val INVALID_ITEM_ID = 0
    }
}