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
import com.brandonjamesyoung.levelup.constants.DATABASE_NAME
import com.brandonjamesyoung.levelup.utility.SnackbarHelper
import com.brandonjamesyoung.levelup.viewmodels.AdvancedSettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.Calendar


@AndroidEntryPoint
class AdvancedSettings : Fragment(R.layout.advanced_settings) {
    private val viewModel: AdvancedSettingsViewModel by activityViewModels()

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

        // Setup source file for copying
        val context = requireContext()
        val dbFile: File = context.getDatabasePath(DATABASE_NAME)
        val contentResolver = context.contentResolver
        val inputStream: FileInputStream = dbFile.inputStream()

        // Setup copy destination
        val outputStream: OutputStream? = contentResolver.openOutputStream(backupLocationUri)

        if (outputStream == null) {
            Log.e(TAG, "Could not create outputStream for backupUri")
            viewModel.showSnackbar(BACKUP_FAIL_MESSAGE)
            inputStream.close()
            return
        }

        // Create copy of the file at the destination
        try {
            outputStream.use { fileOut ->
                inputStream.copyTo(fileOut)
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.message.toString())
            viewModel.showSnackbar(BACKUP_FAIL_MESSAGE)
            inputStream.close()
            outputStream.close()
            return
        }

        viewModel.showSnackbar("Back up successful")
        inputStream.close()
        outputStream.close()
    }

    private fun createLocalDbBackup() {
        // Setup input stream
        val context = requireContext()
        val dbFile: File = context.getDatabasePath(DATABASE_NAME)
        var inputStream: FileInputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            inputStream = dbFile.inputStream()

            // Setup output stream
            val internalDir: File = context.filesDir
            val backupFolder = File(internalDir, LOCAL_BACKUP_DIR_NAME)
            if (!backupFolder.exists()) backupFolder.mkdirs()
            val tempDbBackup = File(backupFolder, TEMP_DB_BACKUP_NAME)
            outputStream = tempDbBackup.outputStream()

            // Copy file over
            outputStream.use { fileOut ->
                inputStream.copyTo(fileOut)
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Local backup failed")
            Log.e(TAG, ex.message.toString())
            viewModel.showSnackbar(RESTORE_DATA_FAIL_MESSAGE)
        }

        inputStream?.close()
        outputStream?.close()
    }

    private fun restoreLocalDbBackup() {
        val context = requireContext()
        var inputStream: FileInputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            val internalDir: File = context.filesDir
            val backupFolder = File(internalDir, LOCAL_BACKUP_DIR_NAME)
            val tempDbFile = File(backupFolder, TEMP_DB_BACKUP_NAME)
            inputStream = tempDbFile.inputStream()

            val dbFile: File = context.getDatabasePath(DATABASE_NAME)
            outputStream = dbFile.outputStream()

            outputStream.use { fileOut ->
                inputStream.copyTo(fileOut)
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Restore local backup file failed")
            Log.e(TAG, ex.message.toString())
            viewModel.showSnackbar(RESTORE_DATA_FAIL_MESSAGE)
        }

        inputStream?.close()
        outputStream?.close()
    }

    private fun deleteLocalBackupDb() {
        val internalDir: File = requireContext().filesDir
        val backupFolder = File(internalDir, LOCAL_BACKUP_DIR_NAME)
        val tempDbFile = File(backupFolder, TEMP_DB_BACKUP_NAME)
        if (tempDbFile.exists()) tempDbFile.delete()
    }

    private fun restoreDb(restoreFileUri: Uri?) {
        if (restoreFileUri == null) {
            Log.d(TAG, "No uri given. Restore data process aborted.")
            return
        }

        Log.i(TAG, "Attempting to restore db data")
        createLocalDbBackup()

        // Setup source file for copying
        val context = requireContext()
        val contentResolver = context.contentResolver
        val inputStream: InputStream?

        try {
            inputStream = contentResolver.openInputStream(restoreFileUri)
        } catch (ex: FileNotFoundException) {
            Log.e(TAG, "Could not create input stream from selected file")
            viewModel.showSnackbar(RESTORE_DATA_FAIL_MESSAGE)
            deleteLocalBackupDb()
            return
        }

        if (inputStream == null) {
            Log.e(TAG, "Could not create input stream from selected file")
            viewModel.showSnackbar(RESTORE_DATA_FAIL_MESSAGE)
            deleteLocalBackupDb()
            return
        }

        // Setup copy destination
        val dbFile: File = context.getDatabasePath(DATABASE_NAME)
        val outputStream: FileOutputStream

        try {
            outputStream = dbFile.outputStream()
        } catch (ex: Exception) {
            Log.e(TAG, "Could not create outputStream from the database")
            viewModel.showSnackbar(RESTORE_DATA_FAIL_MESSAGE)
            inputStream.close()
            deleteLocalBackupDb()
            return
        }

        // Copy database data over
        try {
            outputStream.use { fileOut ->
                inputStream.copyTo(fileOut)
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.message.toString())
            viewModel.showSnackbar(RESTORE_DATA_FAIL_MESSAGE)
            inputStream.close()
            outputStream.close()
            restoreLocalDbBackup()
            deleteLocalBackupDb()
            return
        }

        viewModel.showSnackbar("Restoration successful")
        deleteLocalBackupDb()
        inputStream.close()
        outputStream.close()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            Log.i(TAG, "On Advanced Settings page")
            setupButtons()
            setupObservables()
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
            restoreDb(restoreFileUri)
            resetRestoreDataStatus()
        }
    }

    companion object {
        private const val TAG = "AdvancedSettings"
        private const val BACKUP_FAIL_MESSAGE = "Back up failed"
        private const val RESTORE_DATA_FAIL_MESSAGE = "Data restoration failed"
        private const val LOCAL_BACKUP_DIR_NAME = "TempBackups"
        private const val TEMP_DB_BACKUP_NAME = "TempBackup"
    }
}