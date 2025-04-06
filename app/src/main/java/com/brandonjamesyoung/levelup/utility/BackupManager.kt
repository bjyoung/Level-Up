package com.brandonjamesyoung.levelup.utility

import android.content.Context
import android.net.Uri
import android.util.Log
import com.brandonjamesyoung.levelup.constants.BackupDbError
import com.brandonjamesyoung.levelup.constants.DATABASE_NAME
import com.brandonjamesyoung.levelup.constants.RestoreDbError
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class BackupManager (val context: Context) {
    // Backup local db file to selected uri
    // Returns an error code if something goes wrong
    fun backupDb(backupLocationUri: Uri) : BackupDbError? {
        // Set up db source stream
        val dbFile: File = context.getDatabasePath(DATABASE_NAME)
        val contentResolver = context.contentResolver

        dbFile.inputStream().use { fileIn ->
            // Set up copy destination
            val outputStream: OutputStream = contentResolver.openOutputStream(backupLocationUri)
                ?: return BackupDbError.OUTPUT_STREAM_ERROR

            // Create copy of the file at the destination
            try {
                outputStream.use { fileOut ->
                    fileIn.copyTo(fileOut)
                }
            } catch (ex: Exception) {
                Log.e(TAG, ex.message.toString())
                return BackupDbError.COPY_ERROR
            }
        }

        return null
    }

    // Save selected file to the app directory and return a File for it
    private fun saveFileToAppDir(selectedFileUri: Uri) : File? {
        val inputStream = context.contentResolver.openInputStream(selectedFileUri)

        if (inputStream == null) {
            Log.e(TAG, "Could not create input stream from selected file")
            return null
        }

        val targetFile: File

        inputStream.use { fileIn ->
            val internalDir: File = context.filesDir
            val backupFolder = File(internalDir, LOCAL_BACKUP_DIR_NAME)
            if (!backupFolder.exists()) backupFolder.mkdirs()
            targetFile = File(backupFolder, TEMP_RECOVERY_FILE_NAME)

            targetFile.outputStream().use { fileOut ->
                fileIn.copyTo(fileOut)
            }
        }

        return targetFile
    }

    private fun deleteTempUserFile() {
        val internalDir: File = context.filesDir
        val backupFolder = File(internalDir, LOCAL_BACKUP_DIR_NAME)
        if (!backupFolder.exists()) return
        val tempUserFile = File(backupFolder, TEMP_RECOVERY_FILE_NAME)
        if (tempUserFile.exists()) tempUserFile.delete()
    }

    // Check that the selected file is the expected MIME type
    private fun isValidDbFile(selectedFileUri: Uri) : Boolean {
        val fileType: String? = context.contentResolver.getType(selectedFileUri)
        return fileType != null && fileType == EXPECTED_DB_MIME_TYPE
    }

    // Return File object pointing to the temporary local db copy location
    private fun getLocalDbBackupFile() : File {
        val internalDir: File = context.filesDir
        val backupFolder = File(internalDir, LOCAL_BACKUP_DIR_NAME)
        if (!backupFolder.exists()) backupFolder.mkdirs()
        return File(backupFolder, TEMP_DB_BACKUP_NAME)
    }

    // Create a temporary backup for the db in the app-specific files dir
    // Returns true if successful, false otherwise
    private fun createLocalDbBackup() : Boolean {
        val dbFile: File = context.getDatabasePath(DATABASE_NAME)
        val outputStream: FileOutputStream

        dbFile.inputStream().use { fileIn ->
            try {
                val tempDbBackup: File = getLocalDbBackupFile()
                outputStream = tempDbBackup.outputStream()

                outputStream.use { fileOut ->
                    fileIn.copyTo(fileOut)
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Local backup failed")
                Log.e(TAG, ex.message.toString())
                return false
            }
        }

        return true
    }

    // Restore the database with a local copy of the database
    // Must create local db backup beforehand
    // Returns true if successful, false otherwise
    private fun restoreLocalDbBackup() : Boolean {
        val inputStream: FileInputStream
        val outputStream: FileOutputStream

        try {
            val tempDbFile = getLocalDbBackupFile()
            inputStream = tempDbFile.inputStream()

            val dbFile: File = context.getDatabasePath(DATABASE_NAME)
            outputStream = dbFile.outputStream()

            inputStream.use {
                outputStream.use { fileOut ->
                    inputStream.copyTo(fileOut)
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Restore local backup file failed")
            Log.e(TAG, ex.message.toString())
            return false
        }

        return true
    }

    // Delete the local db backup, if it exists
    fun deleteLocalBackupDb() {
        val internalDir: File = context.filesDir
        val backupFolder = File(internalDir, LOCAL_BACKUP_DIR_NAME)
        if (!backupFolder.exists()) return
        val tempDbFile = File(backupFolder, TEMP_DB_BACKUP_NAME)
        if (tempDbFile.exists()) tempDbFile.delete()
    }

    // Clean up operations done during restoration
    private fun closeDataRestoreResources(inputStream: InputStream, outputStream: OutputStream) {
        deleteLocalBackupDb()
        inputStream.close()
        outputStream.close()
    }

    // Restore database using selected file
    fun restoreData(selectedFileUri: Uri) : RestoreDbError? {
        if (!isValidDbFile(selectedFileUri)) return RestoreDbError.INVALID_FILE

        // Create backup of current database in case something goes wrong
        val localBackupSuccessful: Boolean = createLocalDbBackup()
        if (!localBackupSuccessful) return RestoreDbError.LOCAL_BACKUP_FAILED

        // Setup selected file as input stream
        val contentResolver = context.contentResolver
        Log.i(TAG, "Attempting to restore db data")
        val inputStream: InputStream?

        try {
            inputStream = contentResolver.openInputStream(selectedFileUri)
        } catch (ex: FileNotFoundException) {
            deleteLocalBackupDb()
            return RestoreDbError.INPUT_STREAM_ERROR
        }

        if (inputStream == null) {
            deleteLocalBackupDb()
            return RestoreDbError.INPUT_STREAM_ERROR
        }

        // Setup db as output stream
        val dbFile: File = context.getDatabasePath(DATABASE_NAME)
        val outputStream: FileOutputStream = dbFile.outputStream()

        // Replace database with the selected file
        try {
            outputStream.use { fileOut ->
                inputStream.copyTo(fileOut)
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.message.toString())
            val dbRollbackSuccessful: Boolean = restoreLocalDbBackup()

            if (!dbRollbackSuccessful) {
                Log.e(TAG, "Database rollback unsuccessful")
            }

            closeDataRestoreResources(inputStream, outputStream)
            return RestoreDbError.COPY_ERROR
        }

        closeDataRestoreResources(inputStream, outputStream)
        return null
    }

    companion object {
        private const val TAG = "BackupManager"
        private const val LOCAL_BACKUP_DIR_NAME = "TempBackups"
        private const val TEMP_DB_BACKUP_NAME = "TempBackup"
        private const val TEMP_RECOVERY_FILE_NAME = "TempRecovery"
        private const val EXPECTED_DB_MIME_TYPE = "application/octet-stream"
    }
}