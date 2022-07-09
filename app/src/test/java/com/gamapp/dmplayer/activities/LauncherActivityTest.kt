package com.gamapp.dmplayer.activities

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.google.common.primitives.UnsignedInteger
import junit.framework.TestCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.math.BigDecimal

class LauncherActivityTest : TestCase() {
    data class Item(val id: Int, val title: String)

    @Test
    fun test() {
        val ids = (0..100).shuffled()
        val items = ids.map {
            Item(id = it, (it * 999).toString().toList().shuffled().joinToString { it.toString() })
        }
        println(items)

    }
}