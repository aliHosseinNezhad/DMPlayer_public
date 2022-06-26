package com.gamapp.dmplayer.presenter.ui.utils.pager

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ScrollAxisRange
import androidx.compose.ui.semantics.horizontalScrollAxisRange
import androidx.compose.ui.semantics.scrollBy
import androidx.compose.ui.semantics.selectableGroup
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
//import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Library-wide switch to turn on debug logging.
 */
internal const val DebugLog = false

private const val LogTag = "Pager"


@Composable
fun rememberCustomPagerState(
    @IntRange(from = 0) pageCount: Int,
    @IntRange(from = 0) initialPage: Int = 0,
    @FloatRange(from = 0.0, to = 1.0) initialPageOffset: Float = 0f,
    @IntRange(from = 1) initialOffscreenLimit: Int = 1,
    infiniteLoop: Boolean = false,
): CustomPagerState = rememberSaveable(saver = CustomPagerState.SAVER) {
    CustomPagerState(
        pageCount = pageCount,
        currentPage = initialPage,
        currentPageOffset = initialPageOffset,
        offscreenLimit = initialOffscreenLimit,
        infiniteLoop = infiniteLoop
    )
}.apply {
    this.pageCount = pageCount
}

/**
 * This attempts to mimic ViewPager's custom scroll interpolator. It's not a perfect match
 * (and we may not want it to be), but this seem to match in terms of scroll duration and 'feel'
 */
private const val SnapSpringStiffness = 2750f

@RequiresOptIn(message = "Accompanist Pager is experimental. The API may be changed in the future.")
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalPagerApi

@Immutable
private data class PageData(val page: Int) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@PageData
}

private val Measurable.page: Int
    get() = (parentData as? PageData)?.page ?: error("No PageData for measurable $this")

/**
 * Contains the default values used by [CustomHorizontalPager] and [VerticalPager].
 */
object PagerDefaults {
    /**
     * Create and remember default [FlingBehavior] that will represent the scroll curve.
     *
     * @param stateCustom The [CustomPagerState] to update.
     * @param decayAnimationSpec The decay heart spec to use for decayed flings.
     * @param snapAnimationSpec The heart spec to use when snapping.
     */
    @Composable
    fun defaultPagerFlingConfig(
        stateCustom: CustomPagerState,
        decayAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
        snapAnimationSpec: AnimationSpec<Float> = spring(stiffness = SnapSpringStiffness),
    ): FlingBehavior = remember(stateCustom, decayAnimationSpec, snapAnimationSpec) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(
                initialVelocity: Float,
            ): Float = stateCustom.fling(
                initialVelocity = -initialVelocity,
                decayAnimationSpec = decayAnimationSpec,
                snapAnimationSpec = snapAnimationSpec,
                scrollBy = { deltaPixels -> -scrollBy(-deltaPixels) },
            )
        }
    }
}

/**
 * A horizontally scrolling layout that allows users to flip between items to the left and right.
 *
 * @sample com.google.accompanist.sample.pager.HorizontalPagerSample
 *
 * @param state the state object to be used to control or observe the pager's state.
 * @param modifier the modifier to apply to this layout.
 * @param reverseLayout reverse the direction of scrolling and layout, when `true` items will be
 * composed from the end to the start and [CustomPagerState.currentPage] == 0 will mean
 * the first item is located at the end.
 * @param itemSpacing horizontal spacing to add between items.
 * @param dragEnabled toggle manual scrolling, when `false` the user can not drag the view to a
 * different page.
 * @param flingBehavior logic describing fling behavior.
 * @param content a block which describes the content. Inside this block you can reference
 * [PagerScope.currentPage] and other properties in [PagerScope].
 */
@Composable
fun CustomHorizontalPager(
    state: CustomPagerState,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    itemSpacing: Dp = 0.dp,
    dragEnabled: Boolean = true,
    flingBehavior: FlingBehavior = PagerDefaults.defaultPagerFlingConfig(state),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable PagerScope.(page: Int) -> Unit,
) {
    Pager(
        stateCustom = state,
        modifier = modifier,
        isVertical = false,
        reverseLayout = reverseLayout,
        itemSpacing = itemSpacing,
        verticalAlignment = verticalAlignment,
        horizontalAlignment = horizontalAlignment,
        dragEnabled = dragEnabled,
        flingBehavior = flingBehavior,
        content = content
    )
}

/**
 * A vertically scrolling layout that allows users to flip between items to the top and bottom.
 *
 * @sample com.google.accompanist.sample.pager.VerticalPagerSample
 *
 * @param stateCustom the state object to be used to control or observe the pager's state.
 * @param modifier the modifier to apply to this layout.
 * @param reverseLayout reverse the direction of scrolling and layout, when `true` items will be
 * composed from the bottom to the top and [CustomPagerState.currentPage] == 0 will mean
 * the first item is located at the bottom.
 * @param itemSpacing vertical spacing to add between items.
 * @param dragEnabled toggle manual scrolling, when `false` the user can not drag the view to a
 * different page.
 * @param flingBehavior logic describing fling behavior.
 * @param content a block which describes the content. Inside this block you can reference
 * [PagerScope.currentPage] and other properties in [PagerScope].
 */
@Composable
fun VerticalPager(
    stateCustom: CustomPagerState,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    itemSpacing: Dp = 0.dp,
    dragEnabled: Boolean = true,
    flingBehavior: FlingBehavior = PagerDefaults.defaultPagerFlingConfig(stateCustom),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable PagerScope.(page: Int) -> Unit,
) {
    Pager(
        stateCustom = stateCustom,
        modifier = modifier,
        isVertical = true,
        reverseLayout = reverseLayout,
        itemSpacing = itemSpacing,
        verticalAlignment = verticalAlignment,
        horizontalAlignment = horizontalAlignment,
        dragEnabled = dragEnabled,
        flingBehavior = flingBehavior,
        content = content
    )
}

@Composable
internal fun Pager(
    stateCustom: CustomPagerState,
    modifier: Modifier,
    reverseLayout: Boolean,
    itemSpacing: Dp,
    isVertical: Boolean,
    verticalAlignment: Alignment.Vertical,
    horizontalAlignment: Alignment.Horizontal,
    dragEnabled: Boolean,
    flingBehavior: FlingBehavior,
    content: @Composable PagerScope.(page: Int) -> Unit,
) {
    // True if the scroll direction is RTL, false for LTR
    val reverseDirection = when {
        // If we're vertical, just use reverseLayout as-is
        isVertical -> reverseLayout
        // If we're horizontal in RTL, flip reverseLayout
        LocalLayoutDirection.current == LayoutDirection.Rtl -> !reverseLayout
        // Else (horizontal in LTR), use reverseLayout as-is
        else -> reverseLayout
    }

    val coroutineScope = rememberCoroutineScope()
    val semanticsAxisRange = remember(stateCustom, reverseDirection) {
        ScrollAxisRange(
            value = { stateCustom.currentLayoutPage + stateCustom.currentLayoutPageOffset },
            maxValue = { stateCustom.lastPageIndex.toFloat() },
        )
    }
    val semantics = Modifier.semantics {
        horizontalScrollAxisRange = semanticsAxisRange
        // Hook up scroll actions to our state
        scrollBy { x, y ->
            coroutineScope.launch {
                if (isVertical) {
                    stateCustom.scrollBy(if (reverseDirection) y else -y)
                } else {
                    stateCustom.scrollBy(if (reverseDirection) x else -x)
                }
            }
            true
        }
        // Treat this as a selectable group
        selectableGroup()
    }
    val scrollable = Modifier.scrollable(
        orientation = if (isVertical) Orientation.Vertical else Orientation.Horizontal,
        flingBehavior = flingBehavior,
        reverseDirection = reverseDirection,
        state = stateCustom,
        enabled = dragEnabled
    )

    Layout(
        modifier = modifier
            .then(semantics)
            .then(scrollable)
            // Add a NestedScrollConnection which consumes all post fling/scrolls
            .nestedScroll(connection = ConsumeFlingNestedScrollConnection)
//            .clipScrollableContainer(isVertical)
            ,
        content = {
            if (DebugLog) {
                val firstPage = stateCustom.layoutPages.firstOrNull { it.page != null }
                val lastPage = stateCustom.layoutPages.lastOrNull { it.page != null }
//                Napier.d(
//                    tag = LogTag,
//                    message = "Content: firstPage:${firstPage?.page ?: "none"}, " +
//                            "layoutPage:${state.currentLayoutPageInfo}, " +
//                            "currentPage:${state.currentPage}, " +
//                            "lastPage:${lastPage?.page ?: "none"}"
//                )
            }

            // FYI: We need to filter out null/empty pages *outside* of the loop. Compose uses the
            // call stack as part of the key for state, so we need to ensure that the call stack
            // for page content is consistent as the user scrolls, otherwise content will
            // drop/recreate state.
            val pages = stateCustom.layoutPages.mapNotNull { it.page }
            for (_page in pages) {
                val page = stateCustom.pageOf(_page)
                key(page) {
                    val itemSemantics = Modifier.semantics {
                        this.selected = page == stateCustom.currentPage
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = itemSemantics.then(PageData(_page))
                    ) {
                        val scope = remember(this, stateCustom) {
                            PagerScopeImpl(this, stateCustom)
                        }
                        scope.content(page)
                    }
                }
            }
        },
    ) { measurables, constraints ->
        if (measurables.isEmpty()) {
            // If we have no measurables, no-op and return
            return@Layout layout(constraints.minWidth, constraints.minHeight) {}
        }

        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        val placeables = measurables.map { it.measure(childConstraints) }
        // Our pager width/height is the maximum pager content width/height, and coerce
        // each by our minimum constraints
        val pagerWidth = placeables.maxOf { it.width }.coerceAtLeast(constraints.minWidth)
        val pagerHeight = placeables.maxOf { it.height }.coerceAtLeast(constraints.minHeight)

        layout(width = pagerWidth, height = pagerHeight) {
            val layoutPage = stateCustom.currentLayoutPage
            val offset = stateCustom.currentLayoutPageOffset
            val itemSpacingPx = itemSpacing.roundToPx()

            placeables.forEachIndexed { index, placeable ->
                val page = measurables[index].page
                val layoutInfo = stateCustom.layoutPages.firstOrNull { it.page == page }

                val xCenterOffset = horizontalAlignment.align(
                    size = placeable.width,
                    space = pagerWidth,
                    layoutDirection = layoutDirection,
                )
                val yCenterOffset = verticalAlignment.align(
                    size = placeable.height,
                    space = pagerHeight,
                )

                var yItemOffset = 0
                var xItemOffset = 0
                val offsetForPage = page - layoutPage - offset

                if (isVertical) {
                    layoutInfo?.layoutSize = placeable.height
                    yItemOffset = (offsetForPage * (placeable.height + itemSpacingPx)).roundToInt()
                } else {
                    layoutInfo?.layoutSize = placeable.width
                    xItemOffset = (offsetForPage * (placeable.width + itemSpacingPx)).roundToInt()
                }

                // We can't rely on placeRelative() since that only uses the LayoutDirection, and
                // we need to cater for our reverseLayout param too. reverseDirection contains
                // the resolved direction, so we use that to flip the offset direction...
                placeable.place(
                    x = xCenterOffset + if (reverseDirection) -xItemOffset else xItemOffset,
                    y = yCenterOffset + if (reverseDirection) -yItemOffset else yItemOffset,
                )
            }
        }
    }
}

private object ConsumeFlingNestedScrollConnection : NestedScrollConnection {
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset = when (source) {
        // We can consume all resting fling scrolls so that they don't propagate up to the
        // Pager
        NestedScrollSource.Fling -> available
        else -> Offset.Zero
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        // We can consume all post fling velocity so that it doesn't propagate up to the Pager
        return available
    }
}

/**
 * Scope for [CustomHorizontalPager] content.
 */
@Stable
interface PagerScope : BoxScope {
    /**
     * Returns the current selected page
     */
    val currentPage: Int

    /**
     * Returns the current selected page offset
     */
    val currentPageOffset: Float
}


private class PagerScopeImpl constructor(
    private val boxScope: BoxScope,
    private val stateCustom: CustomPagerState,
) : PagerScope, BoxScope by boxScope {
    override val currentPage: Int get() = stateCustom.currentPage
    override val currentPageOffset: Float get() = stateCustom.currentPageOffset
}

/**
 * Calculate the offset for the given [page] from the current scroll position. This is useful
 * when using the scroll position to apply effects or animations to items.
 *
 * The returned offset can positive or negative, depending on whether which direction the [page] is
 * compared to the current scroll position.
 *
 * @sample com.google.accompanist.sample.pager.HorizontalPagerWithOffsetTransition
 */
fun PagerScope.calculateCurrentOffsetForPage(page: Int): Float {
    return (currentPage + currentPageOffset) - page
}