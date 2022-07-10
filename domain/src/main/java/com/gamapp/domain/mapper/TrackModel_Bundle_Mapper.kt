package com.gamapp.domain.mapper

import android.os.Bundle
import com.gamapp.domain.Constant
import com.gamapp.domain.models.TrackModel
import com.google.android.exoplayer2.extractor.mp4.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.Json


fun TrackModel.bundle(bundle: Bundle = Bundle()): Bundle {
    val json = try {
        val gson = Gson()
        gson.toJson(this)
    } catch (e: Exception) {
        null
    }
    return bundle.apply {
        if (json != null)
            putString(Constant.BundleKey.TrackModelKey, json)
    }
}

fun Bundle?.getTrackModel(): TrackModel? {
    val gson = Gson()
    val json = this?.getString(Constant.BundleKey.TrackModelKey)
    return json?.let {
        try {
            gson.fromJson(json, TrackModel::class.java)
        } catch (e: Exception) {
            null
        }
    }
}