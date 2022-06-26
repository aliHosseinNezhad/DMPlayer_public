package com.gamapp.data.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

object DefaultQueueIds {
    const val Favorite = "Favorite"
    const val Queue = "Queue"
}

@Singleton
class UniqueIdManager @Inject constructor(@ApplicationContext val context: Context) {
    private val file = File(context.filesDir, UniqueIdKey)
    private val type = object : TypeToken<MutableList<String>>() {}.type
    private val gson = Gson()

    init {
        if (!file.exists()) {
            file.createNewFile()
        }
        storeIdUnique(DefaultQueueIds.Favorite)
        storeIdUnique(DefaultQueueIds.Queue)
    }

    private fun storeIdUnique(id: String) {
        if (getAllIds().exist(id)) return
        storeId(id)
    }

    fun generateId(): String {
        val list = getIds()
        val id = list.generateId()
        storeId(id)
        return id
    }

    suspend fun removeId(id: String) {
        withContext(Dispatchers.IO){
            val list = getIds()
            list.remove(id)
            list.store()
        }
    }

    fun getAllIds() = getIds().toList()

    private fun getIds(): MutableList<String> {
        val json = file.readText()
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    private fun storeId(id: String) {
        val list = getIds()
        list.add(id)
        val newJson = gson.toJson(list)
        file.writeText(newJson)
    }

    private fun List<String>.store() {
        val newJson = gson.toJson(this)
        file.writeText(newJson)
    }

    private fun List<String>.exist(id: String): Boolean {
        forEach {
            if (it == id)
                return true
        }
        return false
    }

    private fun List<String>.generateId(): String {
        var id: String
        do {
            id = UUID.randomUUID().toString()
        } while (exist(id))
        return id
    }
}