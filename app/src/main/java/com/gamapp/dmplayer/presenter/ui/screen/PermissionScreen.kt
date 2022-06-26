package com.gamapp.dmplayer.presenter.ui.screen

import android.app.Activity
import android.content.ContextWrapper
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.utils.isPermissionsGranted
import com.gamapp.dmplayer.presenter.ui.theme.primary
import com.gamapp.dmplayer.presenter.utils.navigateBarHeight
import com.gamapp.dmplayer.presenter.utils.permissions
import com.gamapp.domain.usecase.framework.OpenApplicationDetailsUseCase
import com.google.accompanist.insets.statusBarsHeight


@Composable
fun ColumnScope.Contents() {
    val color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Welcome to Music Player",
            modifier = Modifier.fillMaxWidth(0.8f),
            textAlign = TextAlign.Start,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = color
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Music Player also needs the following permissions:",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = color
        )
    }
    Spacer(modifier = Modifier.padding(16.dp))
    Row(
        Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.outline_storage_24),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier
                .size(30.dp)
                .align(CenterVertically)
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Column(
            Modifier
                .weight(1f)
                .align(CenterVertically)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(), contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Storage",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(), contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Access storage to play music files.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.Gray
                )
            }
        }
    }
}


@Composable
fun ColumnScope.Button(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .align(CenterHorizontally)
    ) {
        Card(
            onClick = onClick,
            shape = RoundedCornerShape(50),
            backgroundColor = primary,
            elevation = 2.dp,
            modifier = Modifier
                .width(170.dp)
                .height(50.dp)
                .align(Center)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Start",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Center),
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}



@Composable
fun PermissionScreenCallback(onPermissionGranted: (Boolean) -> Unit) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    LaunchedEffect(key1 = Unit) {
        if (context.isPermissionsGranted())
            onPermissionGranted(true)
    }
    val showPermissionRationalDialog = remember {
        mutableStateOf(false)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        if (context.isPermissionsGranted()) {
            onPermissionGranted(true)
        } else {
            showPermissionRationalDialog.value = true
        }
    }
    PermissionUi(
        showPermissionRationalDialog = showPermissionRationalDialog,
        permissionLauncher = permissionLauncher
    )

}


@Composable
fun PermissionUi(
    showPermissionRationalDialog: MutableState<Boolean>,
    permissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
) {
    val context = LocalContext.current
    val activity = context as Activity
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(CenterStart)
                .padding(horizontal = 32.dp, vertical = 20.dp)
        ) {
            Spacer(modifier = Modifier.statusBarsHeight(32.dp))
            Contents()
            Spacer(modifier = Modifier.weight(1f))
            Button {
                for (permission in permissions) {
                    if (activity.shouldShowRequestPermissionRationale(permission)) {
                        showPermissionRationalDialog.value = true
                    }
                }
                if (!showPermissionRationalDialog.value) {
                    permissionLauncher.launch(permissions.toTypedArray())
                }
            }
            Spacer(modifier = Modifier.height(context.navigateBarHeight()))
        }

    }
    PermissionDialog(showPermissionRationalDialog, activity)
}


@Composable
fun PermissionScreen(content: @Composable () -> Unit) {
    val context = LocalContext.current
    var check by remember {
        mutableStateOf(context.isPermissionsGranted())
    }
    if (check) {
        content()
    } else {
        val showPermissionRationalDialog = remember {
            mutableStateOf(false)
        }
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) {
            if (context.isPermissionsGranted()) {
                check = true
            } else {
                showPermissionRationalDialog.value = true
            }
        }
        PermissionUi(showPermissionRationalDialog = showPermissionRationalDialog, permissionLauncher = permissionLauncher)
    }
}


@Composable
fun PermissionDialog(showPermissionRationalDialog: MutableState<Boolean>, activity: Activity) {
    val show by showPermissionRationalDialog
    if (show)
        WarningDialog(
            title = stringResource(R.string.access_media_store_title),
            message = stringResource(id = R.string.access_media_store_message),
            confirm = "Access",
            onAccept = {
                OpenApplicationDetailsUseCase(activity.application).invoke()
            },
            onDismissRequest = {
                activity.finish()
            })
}


@Composable
fun WarningDialog(
    title: String,
    message: String,
    confirm: String = "Confirm",
    reject: String = "Cancel",
    onAccept: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .graphicsLayer {
                    shadowElevation = 1.dp.toPx()
                    clip = true
                    shape = RoundedCornerShape(25.dp)
                }
                .background(MaterialTheme.colors.surface)
                .padding(vertical = 8.dp)
                .padding(horizontal = 8.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .wrapContentHeight(CenterVertically),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 26.dp)
                    .wrapContentHeight(CenterVertically),
                text = message,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.caption
            )
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
                    .width(230.dp)
                    .height(50.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(
                    confirm to onAccept to primary,
                    reject to onDismissRequest to Color.Gray
                ).forEach {
                    val buttonColor = it.second
                    val scope = it.first.second
                    val text = it.first.first
                    Button(
                        onClick = {
                            scope()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier
                            .width(100.dp)
                            .height(45.dp)
                    ) {
                        Text(
                            text = text,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}
