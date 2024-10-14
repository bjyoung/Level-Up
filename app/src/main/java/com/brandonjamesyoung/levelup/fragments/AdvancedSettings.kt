package com.brandonjamesyoung.levelup.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.BackupDbError
import com.brandonjamesyoung.levelup.constants.RestoreDbError
import com.brandonjamesyoung.levelup.utility.BackupManager
import com.brandonjamesyoung.levelup.utility.SnackbarHelper
import com.brandonjamesyoung.levelup.viewmodels.AdvancedSettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class AdvancedSettings : Fragment(R.layout.advanced_settings) {
    private val viewModel: AdvancedSettingsViewModel by activityViewModels()

    private lateinit var backupManager: BackupManager

    private var backupFileUri: Uri? = null

    private var backupDestReturned: Boolean = false

    private val backupDbCallback = registerForActivityResult(getCreateDocContract()) { uri: Uri? ->
        backupFileUri = uri
        backupDestReturned = true
    }

    private var restoreFileUri: Uri? = null

    private var restoreFilePicked: Boolean = false

    private val restoreDbCallback = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        restoreFileUri = uri
        restoreFilePicked = true
    }

    private fun getCreateDocContract() : ActivityResultContracts.CreateDocument {
        val mimeType = "application/x-sqlite3"
        return ActivityResultContracts.CreateDocument(mimeType)
    }

    private fun navigateToAboutPage() {
        findNavController().navigate(R.id.action_advancedSettings_to_about)
        Log.i(TAG, "Going from Advanced Settings to About")
    }

    private fun setupAboutButton() {
        val view = requireView()
        val aboutButton = view.findViewById<Button>(R.id.AboutButton)

        aboutButton.setOnClickListener{
            navigateToAboutPage()
        }
    }

    private fun getCurrentDate(): String {
        val today = Calendar.getInstance()
        val currYear = today.get(Calendar.YEAR)
        val currMonth = today.get(Calendar.MONTH)
        val currDay = today.get(Calendar.DAY_OF_MONTH)
        val separator = "_"
        return "$currMonth$separator$currDay$separator$currYear"
    }

    private fun backupData(backupLocationUri: Uri?) {
        if (backupLocationUri == null) {
            Log.d(TAG, "No uri given. Backup aborted.")
            return
        }

        Log.i(TAG, "Attempting to back up data")
        val backupDbError = backupManager.backupDb(backupLocationUri)

        if (backupDbError != null) {
            if (backupDbError == BackupDbError.OUTPUT_STREAM_ERROR) {
                Log.e(TAG, "Could not create output stream")
            } else if (backupDbError == BackupDbError.COPY_ERROR) {
                Log.e(TAG, "Something went wrong with copying the backup file")
            }

            viewModel.showSnackbar(BACKUP_FAIL_MESSAGE)
            return
        }

        viewModel.showSnackbar("Back up successful")
    }

    // Restore database using the selected file
    private fun restoreData(restoreFileUri: Uri?) {
        if (restoreFileUri == null) {
            Log.d(TAG, "No uri given. Restore data process aborted.")
            return
        }

        val restoreDbError = backupManager.restoreData(restoreFileUri)

        if (restoreDbError != null) {
            when (restoreDbError) {
                RestoreDbError.LOCAL_BACKUP_FAILED -> {
                    Log.e(TAG, "Local database backup failed")
                }
                RestoreDbError.INPUT_STREAM_ERROR -> {
                    Log.e(TAG, "Could not create input stream from selected file")
                }
                RestoreDbError.COPY_ERROR -> {
                    Log.e(TAG, "Something went wrong with replacing the existing db")
                }
            }

            viewModel.showSnackbar(RESTORE_DATA_FAIL_MESSAGE)
            return
        }

        viewModel.showSnackbar("Date restoration successful")
    }

    private fun generateBackupFileName() : String {
        return "LVL_UP_BACKUP_${getCurrentDate()}.bkp"
    }

    private fun onBackupButtonTrigger() {
        val backupFileName = generateBackupFileName()
        backupDbCallback.launch(backupFileName)
    }

    private fun setupBackupButton() {
        val view = requireView()
        val backupButton = view.findViewById<Button>(R.id.BackupButton)

        backupButton.setOnClickListener{
           onBackupButtonTrigger()
        }
    }

    private fun setupRestoreDataButton() {
        val view = requireView()
        val importButton = view.findViewById<Button>(R.id.RestoreDataButton)

        importButton.setOnClickListener{
            restoreDbCallback.launch("*/*")
        }
    }

    private fun navigateToSettings() {
        findNavController().navigate(R.id.action_advancedSettings_to_settings)
        Log.i(TAG, "Going from Advanced Settings to Settings")
    }

    private fun setupBackButton() {
        val view = requireView()
        val backButton = view.findViewById<Button>(R.id.AdvancedSettingsBackButton)

        backButton.setOnClickListener{
            navigateToSettings()
        }
    }

    private fun setupRestoredDefaultsButton() {
        val view = requireView()
        val restoreDefaultsButton = view.findViewById<Button>(R.id.RestoreDefaultsButton)

        restoreDefaultsButton.setOnClickListener{
            viewModel.showSnackbar("Not implemented yet")
        }
    }

    private fun setupButtons() {
        setupBackButton()
        setupAboutButton()
        setupBackupButton()
        setupRestoreDataButton()
        setupRestoredDefaultsButton()
    }

    private fun setupObservables() {
        val view = requireView()

        viewModel.message.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                val confirmButton: View = view.findViewById(R.id.AdvancedSettingsBackButton)
                SnackbarHelper.showSnackbar(it, view, confirmButton)
            }
        }
    }

    private fun resetBackupStatus() {
        backupFileUri = null
        backupDestReturned = false
    }

    private fun resetRestoreDataStatus() {
        restoreFileUri = null
        restoreFilePicked = false
    }

    override fun onResume() {
        super.onResume()

        if (backupDestReturned) {
            backupData(backupFileUri)
            resetBackupStatus()
        }

        if (restoreFilePicked) {
            restoreData(restoreFileUri)
            backupManager.deleteLocalBackupDb()
            resetRestoreDataStatus()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            Log.i(TAG, "On Advanced Settings page")
            backupManager = BackupManager(requireContext())
            setupButtons()
            setupObservables()
        }
    }

    companion object {
        private const val TAG = "AdvancedSettings"
        private const val BACKUP_FAIL_MESSAGE = "Back up failed"
        private const val RESTORE_DATA_FAIL_MESSAGE = "Data restoration failed"
    }
}