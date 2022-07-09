package com.gamapp.dmplayer.framework.data_source.update

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import com.gamapp.data.data_source.media_store.MediaStoreUpdateTrackDataSource
import com.gamapp.dmplayer.presenter.utils.toAudioUri
import com.gamapp.domain.ActivityRegisterResultProvider
import com.gamapp.domain.mapper.toContentValue
import com.gamapp.domain.models.TrackModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import javax.inject.Inject

class MediaStoreUpdateTrackDataSourceImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val activityResultRegistry: Lazy<ActivityResultRegistry?>
) : MediaStoreUpdateTrackDataSource {
    override suspend fun update(ids: List<Long>, contentValues: ContentValues): Unit =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.updateR(ids, contentValues, activityResultRegistry.value)
        } else {
            context.update(ids, contentValues)
        }
}

@RequiresApi(Build.VERSION_CODES.R)
suspend fun Context.updateR(
    ids: List<Long>,
    contentValues: ContentValues,
    activityRegister: ActivityResultRegistry?
): Unit = withContext(Dispatchers.IO) {
    val channel = Channel<Boolean>()
    val key = UUID.randomUUID().toString()
    if (activityRegister == null)
        return@withContext
    val launcher =
        activityRegister.register(key, ActivityResultContracts.StartIntentSenderForResult()) {
            val hasPermission = it.resultCode == Activity.RESULT_OK
            channel.trySend(hasPermission)
        }
    val idsToGrantPermission = ids.filter {
        DocumentFile.fromSingleUri(applicationContext, it.toAudioUri())?.canWrite() == false
    }
    val sender =
        MediaStore.createWriteRequest(
            contentResolver,
            idsToGrantPermission.map { it.toAudioUri() }).intentSender
    launcher.launch(IntentSenderRequest.Builder(sender).build())
    if (channel.receive()) {
        update(ids, contentValues)
    }
    launcher.unregister()
}

suspend fun Context.update(ids: List<Long>, contentValues: ContentValues) {
    withContext(Dispatchers.IO) {
        val resolver = applicationContext.contentResolver
        val selection = "${MediaStore.Audio.Media._ID} = ?"
        ids.forEach {
            resolver.update(it.toAudioUri(), contentValues, selection, arrayOf(it.toString()))
        }
        resolver.notifyChange(Uri.parse("content://media"), null)
    }
}


