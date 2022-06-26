package com.gamapp.dmplayer.presenter.ui.screen.elements.sort_elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamapp.custom.CustomIconButton
import com.gamapp.dmplayer.R
import com.gamapp.dmplayer.presenter.ui.screen.popup_menu_layout.SortPopupMenu
import com.gamapp.dmplayer.presenter.ui.theme.onContent
import com.gamapp.domain.models.ImagedItemModel
import com.gamapp.domain.sealedclasses.*
import kotlinx.coroutines.flow.Flow

@Composable
fun <T : ImagedItemModel> RowScope.SortItem(
    sort: Flow<Sort<T>>,
    setOrder: (Order) -> Unit,
    onSelected: (Sort.Type<T>) -> Unit,
    enabled: Boolean = true
) {
    val current by sort.collectAsState(initial = null)
    val type = current?.type
    val title = type?.name?:""
    val order = current?.order
    val types = current?.types?: emptyList()
    val show = remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier
            .height(30.dp)
            .clip(CircleShape)
            .align(CenterVertically)
            .wrapContentWidth()
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .clip(CircleShape)
                .height(30.dp)
                .clickable(enabled = enabled) {
                    show.value = true
                }
        ) {
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                modifier = Modifier
                    .align(CenterVertically)
                    .size(20.dp),
                contentDescription = null,
                tint = MaterialTheme.colors.onContent,
                painter = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.ic_category))
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = title,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(CenterVertically),
                maxLines = 1,
                fontSize = 14.sp,
                color = MaterialTheme.colors.onContent
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
        Divider(
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = MaterialTheme.colors.onContent.copy(0.4f)
        )
        CustomIconButton(
            onClick = {
                order?.switch()?.let {
                    setOrder(it)
                }
            },
            enabled = enabled,
            modifier = Modifier
                .width(40.dp)
                .wrapContentWidth(CenterHorizontally)
                .size(30.dp)
        ) {
            SortPopupMenu(
                show = show, items = types,
                selected = type,
                onSelect = {
                    onSelected(this)
                }
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_down_1),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        rotationX = if (order == Order.STE) 0f else 180f
                    },
                tint = MaterialTheme.colors.onContent
            )
        }

    }
}