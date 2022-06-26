package com.gamapp.dmplayer.presenter.utils

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import com.gamapp.domain.models.TrackModel

class SelectionManager<T> {
    var selectionList = mutableStateListOf<T>()
    var onSelectionMode by mutableStateOf(false)
    var enabled: Boolean = true
    fun onLongClick(item: T) {
        if (!onSelectionMode && enabled) {
            selectionList.clear()
            selectionList.add(item)
            onSelectionMode = true
        }
    }

    fun cancelSelection() {
        selectionList.clear()
        onSelectionMode = false
    }

    fun contain(item: T) = selectionList.contains(item)

    fun onClick(item: T) {
        if (selectionList.contains(item)) {
            selectionList.remove(item)
        } else {
            selectionList.add(item)
        }
    }


    fun addAll(items: List<T>) {
        selectionList.clear()
        selectionList.addAll(items)
    }

    private val _isAllSelected: MutableState<Boolean> = mutableStateOf(false)
    val isAllSelected:State<Boolean> = _isAllSelected
    @Composable
    fun isAllSelected(size: Int) = selectionList.size == size

    @Composable
    fun isAllSelected(items: List<T>): Boolean {
        return items.containsAll(selectionList) && items.size == selectionList.size
    }
    @Composable
    fun Init(items:List<T>) {
        val allSelected = isAllSelected(items = items)
        LaunchedEffect(key1 = allSelected){
            this@SelectionManager._isAllSelected.value = allSelected
        }
        BackHandler(onSelectionMode) {
            cancelSelection()
        }
    }

    fun eraseAll() = selectionList.clear()
}