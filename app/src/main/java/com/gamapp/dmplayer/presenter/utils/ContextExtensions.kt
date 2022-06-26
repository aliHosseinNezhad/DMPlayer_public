package com.gamapp.dmplayer.presenter.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat


val permissions = mutableListOf<String>().apply {
    this += Manifest.permission.READ_EXTERNAL_STORAGE
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        this += Manifest.permission.ACCESS_MEDIA_LOCATION
    }
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        this += Manifest.permission.WRITE_EXTERNAL_STORAGE
    }
}

fun Context.isPermissionsGranted(): Boolean {
    return checkPermissions(*permissions.toTypedArray())
}


fun Context.checkPermissions(
    vararg permissions: String
): Boolean {
    for (permission in permissions) {
        if (!checkPermission(permission)) return false
    }
    return true
}

fun Context.checkPermission(
    permission: String
): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}

fun Context.shareMusic(id: Long) {
    val uri = id.toAudioUri()
    val share = Intent(Intent.ACTION_SEND)
    share.type = "audio/*"
    share.putExtra(Intent.EXTRA_STREAM, uri)
    startActivity(Intent.createChooser(share, "Share Sound File").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}

fun Context.openMusicWith(id: Long) {
    val uri = id.toAudioUri()
    val musicIntent = Intent(Intent.ACTION_VIEW);
    musicIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
    musicIntent.setDataAndType(uri, "audio/*");
    musicIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(musicIntent)
}

fun Context.navigateBarHeight(): Dp {
    val hasNav = hasNavBar()
    val height = if (hasNav) getNavigationBarHeight() else 0
    val density = this.resources.displayMetrics.density
    return (height / density).dp
}

fun Context.hasNavBar(): Boolean {
    val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
    return id > 0 && resources.getBoolean(id)
}

fun Context.statusBarHeight(): Dp {
    val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
    val height = if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
    val density = resources.displayMetrics.density
    return (height / density).dp
}

fun Context.getNavigationBarHeight(): Int {
    var result = 0
    val resourceId: Int = resources
        .getIdentifier("navigation_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}