package com.gamapp.dmplayer.framework.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.gamapp.dmplayer.presenter.utils.toAudioUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

fun version(check: Int): Boolean {
    return Build.VERSION.SDK_INT == check
}

@RequiresApi(Build.VERSION_CODES.R)
suspend fun Context.removeTracksR(
    tracks: List<Long>,
    registry: ActivityResultRegistry
): Unit = withContext(Dispatchers.IO) {
    val intentSender = MediaStore.createDeleteRequest(contentResolver, tracks.map {
        it.toAudioUri()
    }).intentSender
    val key = UUID.randomUUID().toString()
    val launcher = registry.register(key, ActivityResultContracts.StartIntentSenderForResult()) {

    }
    launcher.launch(IntentSenderRequest.Builder(intentSender).build())
    launcher.unregister()
}

@Suppress("Deprecation")
suspend fun Context.removeTracks(tracks: List<Long>): Unit =
    withContext(Dispatchers.IO) {
        val resolver = contentResolver
        val projection = arrayOf(BaseColumns._ID, MediaStore.MediaColumns.DATA)
        val selection = MediaStore.Audio.AudioColumns._ID + " IN (${
            tracks.map { it.toString() }.joinToString(separator = " , ") { it }
        })"
        var deletedCount = 0
        try {
            val cursor: Cursor? = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection,
                null, null
            )
            if (cursor != null) {
                // Step 2: Remove files from card
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val id: Int = cursor.getInt(0)
                    val name: String = cursor.getString(1)
                    try { // File.delete can throw a security exception
                        val file = File(name)
                        if (file.delete()) {
                            // Step 3: Remove selected track from the database
                            resolver.delete(
                                ContentUris.withAppendedId(
                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                    id.toLong()
                                ), null, null
                            )
                            deletedCount++
                        } else {
                            // I'm not sure if we'd ever get here (deletion would
                            // have to fail, but no exception thrown)
                            Log.e("MusicUtils", "Failed to delete file $name")
                        }
                        cursor.moveToNext()
                    } catch (ex: SecurityException) {
                        cursor.moveToNext()
                    } catch (e: NullPointerException) {
                        Log.e("MusicUtils", "Failed to find file $name")
                    }
                }
                cursor.close()
            }
        } catch (ignored: SecurityException) {
        }
        resolver.notifyChange(Uri.parse("content://media"), null)
    }