package com.brandonjamesyoung.levelup.validation

import android.content.res.Resources
import android.util.Log
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.brandonjamesyoung.levelup.R

private const val MAX_PLAYER_NAME_LENGTH = 7

private val PLAYER_NAME_VALIDATION_REGEX = Regex("^[a-zA-Z\\d\\p{L}]+$")

private const val MAX_QUEST_NAME_LENGTH = 40

private const val MAX_ITEM_NAME_LENGTH = 40

private val QUEST_NAME_VALIDATION_REGEX = Regex("^[a-zA-Z\\d\\p{L}'\"!#$%&:?,.() @_+/*-]+$")

private const val MAX_ACRONYM_LENGTH = 3

private val ACRONYM_VALIDATION_REGEX = Regex("^[a-zA-Z]+$")

class InputValidator {
    // TODO can probably extract out similar lines of code here and in validateQuestName
    fun isValidPlayerName(nameField: EditText, tag: String, fragment: Fragment) : Boolean {
        val name = nameField.text.trim().toString()
        val hasValidLength = name.length <= MAX_PLAYER_NAME_LENGTH

        if (name == "") return true

        if (!hasValidLength) {
            nameField.error = fragment.resources.getString(R.string.player_name_length_error)
            Log.e(tag, "Name is longer than $MAX_PLAYER_NAME_LENGTH characters")
            return false
        }

        val hasValidCharacters = PLAYER_NAME_VALIDATION_REGEX.matches(name)

        if (!hasValidCharacters) {
            nameField.error = fragment.resources.getString(
                R.string.player_name_invalid_char_error
            )
            Log.e(tag, "Player name has invalid characters in it")
            return false
        }

        return true
    }

    fun isValidItemName(nameField: EditText, tag: String, fragment: Fragment) : Boolean {
        return isValidName(MAX_ITEM_NAME_LENGTH, nameField, tag, fragment)
    }

    fun isValidQuestName(nameField: EditText, tag: String, fragment: Fragment) : Boolean {
        return isValidName(MAX_QUEST_NAME_LENGTH, nameField, tag, fragment)
    }

    private fun isValidName(
        maxLength: Int,
        nameField: EditText,
        tag: String,
        fragment: Fragment
    ) : Boolean {
        val name = nameField.text.trim().toString()
        val hasValidLength = name.length <= maxLength
        if (name == "") return true

        if (!hasValidLength) {
            nameField.error = fragment.resources.getString(R.string.quest_name_length_error)
            Log.e(tag, "Name is longer than $maxLength characters")
            return false
        }

        val hasValidCharacters = QUEST_NAME_VALIDATION_REGEX.matches(name)

        if (!hasValidCharacters) {
            nameField.error = fragment.resources.getString(R.string.name_invalid_char_error)
            Log.e(tag, "Name has invalid characters in it")
            return false
        }

        return true
    }

    private fun isNumber(str : String) : Boolean {
        return str.toIntOrNull() != null
    }

    fun isValidNum(
        editText : EditText,
        minNumber : Int? = null,
        maxNumber : Int? = null,
        emptyValuesAllowed: Boolean = false,
        resources : Resources
    ) : Boolean {
        val textInput = editText.text.toString()

        if (emptyValuesAllowed && textInput.isBlank()) {
            return true
        } else if (textInput.isBlank() || !isNumber(textInput)) {
            editText.error = resources.getString(R.string.not_a_number_error)
            return false
        }

        val numInput = textInput.toInt()

        if (minNumber != null && maxNumber != null && numInput !in minNumber..maxNumber) {
            editText.error = resources.getString(
                R.string.num_out_of_range_error,
                minNumber,
                maxNumber
            )

            return false
        }

        if (minNumber != null && maxNumber == null && numInput < minNumber) {
            editText.error = resources.getString(R.string.num_too_small_error, minNumber)
            return false
        }

        if (minNumber == null && maxNumber != null && numInput > maxNumber) {
            editText.error = resources.getString(R.string.num_too_large_error, maxNumber)
            return false
        }

        return true
    }

    fun isValidAcronym(acronymField: EditText, resources: Resources) : Boolean {
        val textInput = acronymField.text.toString()

        if (textInput.isBlank()) {
            acronymField.error = resources.getString(R.string.no_acronym_error)
            return false
        }

        val hasOnlyAlphabet = ACRONYM_VALIDATION_REGEX.matches(textInput)

        if (!hasOnlyAlphabet) {
            acronymField.error = resources.getString(R.string.only_alpha_allowed_error)
            return false
        }

        if (textInput.length > MAX_ACRONYM_LENGTH) {
            acronymField.error = resources.getString(R.string.three_characters_limit_error)
            return false
        }

        return true
    }
}