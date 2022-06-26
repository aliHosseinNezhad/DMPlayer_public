package com.gamapp.dmplayer.presenter.ui.screen.player.buttons.menu_buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.popup_menu_layout.PlayerMenu


@Composable
fun SettingButton(nav:NavHostController) {
    val showSettingMenu = remember {
        mutableStateOf(false)
    }
    CustomIconButton(
        onClick = {
            showSettingMenu.value = true
        }, modifier = Modifier
            .size(50.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                painter = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.ic_more_vert)),
                contentDescription = null,
                modifier = Modifier
                    .size(15.dp)
                    .align(Alignment.Center),
                tint = MaterialTheme.colors.onBackground.copy(0.8f)
            )
            PlayerMenu(show = showSettingMenu, nav)
        }
    }
}