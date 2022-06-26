package com.gamapp.layout.categorypager

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.foundation.lazy.layout.LazyLayoutMeasureScope
import androidx.compose.foundation.lazy.layout.LazyLayoutPrefetchState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints

internal class CategoryPagerItemProvider :
    LazyLayoutItemProvider {
    override val itemCount: Int
        get() = 3

    @Composable
    override fun Item(index: Int) {
        when (index) {
            0 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Magenta)
                ) {
                    LogDispose(index = 1)
                }
            }
            1 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Blue)
                ) {
                    LogDispose(index = 2)
                }
            }
            2 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Green)
                ) {
                    LogDispose(index = 3)
                }
            }
        }
    }

}

@Composable
fun LogDispose(index: Int) {
    DisposableEffect(key1 = Unit){
        Log.i(TAG, "Item: $index")
        onDispose {
            Log.i(TAG, "Item: $index dispose")
        }
    }
}


@Composable
fun rememberLazyCategoryPagerMeasurePolicy() =
    remember<LazyLayoutMeasureScope.(Constraints) -> MeasureResult> {
        {
            val placeables1 = measure(0, it)
            val placeables2 = measure(1, it)
            val placeables3 = measure(2, it)
            val placeables = placeables1 + placeables2 + placeables3
            val cw = placeables.maxOfOrNull { it.width } ?: 0
            val ch = placeables.maxOfOrNull { it.height } ?: 0
            val width = cw.coerceIn(it.minWidth, it.maxWidth)
            val height = ch.coerceIn(it.minHeight, it.maxHeight)
            layout(width, height) {
                var x = 0
                placeables[0].place(0,0)
            }
        }
    }


@Composable
fun LazyCategoryPager(modifier: Modifier) {
    val itemProvider = remember {
        CategoryPagerItemProvider()
    }
    LazyLayout(
        itemProvider = itemProvider,
        measurePolicy = rememberLazyCategoryPagerMeasurePolicy(),
        prefetchState = LazyLayoutPrefetchState(),
        modifier = Modifier.then(modifier)
    )
}


@Preview
@Composable
fun TestLazyCategoryPager() {
    LazyCategoryPager(modifier = Modifier.fillMaxSize())
}