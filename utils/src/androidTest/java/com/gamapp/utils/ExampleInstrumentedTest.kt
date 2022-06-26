package com.gamapp.utils

import android.content.Context
import android.media.*
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import cafe.adriel.androidaudioconverter.AndroidAudioConverter
import cafe.adriel.androidaudioconverter.callback.IConvertCallback
import cafe.adriel.androidaudioconverter.callback.ILoadCallback
import cafe.adriel.androidaudioconverter.model.AudioFormat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest

import org.junit.Test
import org.junit.internal.runners.statements.Fail
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream

import java.io.File
import java.lang.Exception
import java.lang.IndexOutOfBoundsException

sealed class Result<T> {
    class Failure<T>(val message: String) : Result<T>()
    class Success<T>(val data: T) : Result<T>()

    companion object {
        fun <T> success(data: T): Success<T> {
            return Success(data)
        }

        fun <T> failure(message: String): Failure<T> {
            return Failure(message)
        }
    }
}


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
const val TAG = "ExampleAndroidConvertAudio"


@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
//    fun useAppContext() {
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//        val inputStream = context.resources.assets.open("music.mp3")
//        val byteArray = inputStream.readBytes()
//        val file = File(context.filesDir, "music.mp3")
//        runTest {
//            file.outputStream().write(byteArray)
//        }
//        val converter = AndroidAudioConverter.with(context)
//        converter.setFormat(AudioFormat.WAV).setFile(file)
//        val channel = Channel<Result<File?>>()
//        val isReadyChannel = Channel<Result<Unit>>()
//        runBlocking {
//            launch {
//                converter.setCallback(object : IConvertCallback {
//                    override fun onSuccess(convertedFile: File?) {
//                        channel.trySendBlocking(Result.success(convertedFile))
//                    }
//
//                    override fun onFailure(error: Exception?) {
//                        channel.trySendBlocking(Result.failure(error?.message ?: ""))
//                    }
//                })
//                AndroidAudioConverter.load(context, object : ILoadCallback {
//                    override fun onSuccess() {
//                        Log.i(TAG, "onSuccess: success")
//                        isReadyChannel.trySendBlocking(Result.success(Unit))
//                    }
//
//                    override fun onFailure(error: Exception?) {
//                        Log.i(TAG, "onFailure: ${error?.message}")
//                        isReadyChannel.trySend(Result.failure(error?.message ?: ""))
//                    }
//                })
//                if (isReadyChannel.receive() is Result.Success)
//                    converter.convert()
//            }
//
//            channel.receiveAsFlow().collect {
//                when (it) {
//                    is Result.Success -> {
//                        val bytes = it.data?.inputStream()?.readBytes()
//                        Log.i(TAG, "useAppContext: $bytes")
//                    }
//                    is Result.Failure -> {
//                        val message = it.message
//                        Log.i(TAG, "useAppContext: $message")
//                    }
//                }
//            }
//        }
//
//    }


    fun data() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val assetFd = context.resources.assets.openFd("music.mp3")
        val bytes = context.resources.assets.open("music.mp3").readBytes()
        val string = bytes.map { it.toString() }.joinToString("\n") { it }
        val file = File(context.filesDir, "music.txt")
        if (!file.exists()) file.createNewFile()
        file.writeText(string)
    }
}


fun Context.byteArrays(name: String): ByteArray {
    return resources.assets.open(name).readBytes()
}
