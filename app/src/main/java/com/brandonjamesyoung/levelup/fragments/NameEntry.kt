package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Player
import com.brandonjamesyoung.levelup.validation.InputValidator
import com.brandonjamesyoung.levelup.viewmodels.NameEntryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NameEntry : Fragment(R.layout.name_entry) {
    private val viewModel: NameEntryViewModel by activityViewModels()

    @Inject lateinit var validator: InputValidator

    private fun validateInput() : Boolean {
        val view = requireView()
        val nameView = view.findViewById<EditText>(R.id.PlayerNameInput)
        return validator.isValidPlayerName(nameView, TAG, this)
    }

    private fun saveName() {
        val view = requireView()
        val nameInput = view.findViewById<EditText>(R.id.PlayerNameInput)
        var name: String? = nameInput.text.trim().toString()
        if (name == "") name = null
        viewModel.update(name)
    }

    private fun navigateToQuestList() {
        findNavController().navigate(R.id.action_nameEntry_to_questList)
        Log.i(TAG, "Going from Name Entry to Quest List")
    }

    private fun loadName(player: Player) {
        val view = requireView()
        val nameInput = view.findViewById<EditText>(R.id.PlayerNameInput)
        nameInput.setText(player.name)
    }

    private fun setupNameField() {
        viewModel.player.observe(viewLifecycleOwner) {
            loadName(it)
        }
    }

    private fun setupConfirmButton() {
        val view = requireView()
        val saveButton = view.findViewById<AppCompatButton>(R.id.ConfirmButton)

        saveButton.setOnClickListener {
            if (validateInput()){
                saveName()
                viewModel.triggerNameEnteredFlag()
                navigateToQuestList()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            Log.i(TAG, "On Name Entry page")
            setupConfirmButton()
            setupNameField()
        }
    }

    companion object {
        private const val TAG = "NameEntry"
    }
}