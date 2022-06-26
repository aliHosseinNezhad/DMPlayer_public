package com.gamapp.dmplayer.activities

import junit.framework.TestCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

class LauncherActivityTest : TestCase() {

    @Test
    fun test() {
        runBlocking {
            launch {
                val channel = Channel<Int?>()
                launch {
                    println("send started")
                    channel.send(2)
                    println("send finished")
                }
                launch {
                    println("receive started")
                    channel.tryReceive()
                    println("receive finished")
                }
            }
        }
    }
}