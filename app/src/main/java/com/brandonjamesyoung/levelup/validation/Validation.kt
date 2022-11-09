package com.brandonjamesyoung.levelup.validation

import android.util.Log
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.brandonjamesyoung.levelup.R

private const val MAX_QUEST_NAME_LENGTH = 40
private val NAME_VALIDATION_REGEX = Regex("^[0-9a-zA-Z'\"!#$%&:?,.() @_+/*-]+$")
private const val MAX_ACRONYM_LENGTH = 3
private val ACRONYM_VALIDATION_REGEX = Regex("^[a-zA-Z]+$")

class Validation {
    companion object {
        fun validateName(nameField : EditText, tag: String, fragment: Fragment) : Boolean {
            val name = nameField.text.trim().toString()
            val hasValidLength = name.length <= MAX_QUEST_NAME_LENGTH

            if (name == "") {
                return true
            }

            if (!hasValidLength) {
                nameField.error = fragment.resources.getString(R.string.name_length_error)
                Log.e(tag, "Name is longer than 40 characters")
                return false
            }

            val hasValidCharacters = NAME_VALIDATION_REGEX.matches(name)

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

        fun validateNumField(
            editText : EditText,
            minNumber : Int? = null,
            maxNumber : Int? = null,
            fragment : Fragment
        ) : Boolean {
            val textInput = editText.text.toString()

            if (textInput.isBlank() || !isNumber(textInput)) {
                editText.error = fragment.resources.getString(R.string.not_a_number_error)
                return false
            }

            val numInput = textInput.toInt()

            if (minNumber != null && maxNumber != null && numInput !in minNumber..maxNumber) {
                editText.error = fragment.resources.getString(
                    R.string.num_out_of_range_error,
                    minNumber,
                    maxNumber
                )
                return false
            }

            if (minNumber != null && maxNumber == null && numInput < minNumber) {
                editText.error = fragment.resources.getString(
                    R.string.num_too_small_error,
                    minNumber
                )

                return false
            }

            if (minNumber == null && maxNumber != null && numInput > maxNumber) {
                editText.error = fragment.resources.getString(
                    R.string.num_too_large_error,
                    maxNumber
                )

                return false
            }

            return true
        }

        fun validateAcronymField(acronymField: EditText, fragment: Fragment) : Boolean {
            val textInput = acronymField.text.toString()

            if (textInput.isBlank()) {
                acronymField.error = fragment.resources.getString(R.string.no_acronym_error)
                return false
            }

            val hasOnlyAlphabet = ACRONYM_VALIDATION_REGEX.matches(textInput)

            if (!hasOnlyAlphabet) {
                acronymField.error = fragment.resources.getString(R.string.only_alpha_allowed_error)
                return false
            }

            if (textInput.length > MAX_ACRONYM_LENGTH) {
                acronymField.error = fragment.resources.getString(
                    R.string.three_characters_limit_error
                )

                return false
            }

            return true
        }
    }
}