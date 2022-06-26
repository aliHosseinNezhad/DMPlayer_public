package com.gamapp.dmplayer.presenter.ui.utils.pager

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * In the scrollable containers we want to clip the main axis sides in order to not display the
 * content which is scrolled out. But once we apply clipToBounds() modifier on such containers it
 * causes unexpected behavior as we also clip the content on the cross axis sides. It is
 * unexpected as Compose components are not clipping by default. The most common case how it
 * could be reproduced is a horizontally scrolling list of Cards. Cards have the elevation by
 * default and such Cards will be drawn with clipped shadows on top and bottom. This was harder
 * to reproduce in the Views system as usually scrolling containers like RecyclerView didn't have
 * an opaque background which means the ripple was drawn on the surface on the first parent with
 * background. In Compose as we don't clip by default we draw shadows right in place.
 * We faced similar issue in Compose already with Androids Popups and Dialogs where we decided to
 * just predefine some constant with a maximum elevation size we are not going to clip. We are
 * going to reuse this technique here. This will improve how it works in most common cases. If the
 * user will need to have a larger unclipped area for some reason they can always add the needed
 * padding inside the scrollable area.
 *
 * Copied from https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/foundation/foundation/src/commonMain/kotlin/androidx/compose/foundation/Scroll.kt
 */
internal fun Modifier.clipScrollableContainer(isVertical: Boolean) = clip(
    shape = if (isVertical) VerticalClipShape else HorizontalClipShape
)

private val MaxSupportedElevation = 30.dp

private object HorizontalClipShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val inflateSize = with(density) { MaxSupportedElevation.roundToPx().toFloat() }
        return Outline.Rectangle(
            Rect(
                left = 0f,
                top = -inflateSize,
                right = size.width,
                bottom = size.height + inflateSize
            )
        )
    }
}

private object VerticalClipShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val inflateSize = with(density) { MaxSupportedElevation.roundToPx().toFloat() }
        return Outline.Rectangle(
            Rect(
                left = -inflateSize,
                top = 0f,
                right = size.width + inflateSize,
                bottom = size.height
            )
        )
    }
}