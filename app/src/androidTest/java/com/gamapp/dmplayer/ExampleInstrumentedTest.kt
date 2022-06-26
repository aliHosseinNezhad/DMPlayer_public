package com.gamapp.dmplayer

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gamapp.dmplayer.framework.data_source.FakeMediaStoreFetchDataSource
import com.gamapp.dmplayer.framework.data_source.LiveTracks
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

const val TAG = "ExampleInstrumentedTestTAG"


@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun useAppContext() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val fetchDataSource = FakeMediaStoreFetchDataSource(context)
        runBlocking {
            launch {
                flow {
                    delay(10)
                    emit(Unit)
                    delay(1000)
                    emit(Unit)
                    delay(1000)
                    emit(Unit)
                }.collect {
                    fetchDataSource.liveTracks.tryEmit(LiveTracks.Expired)
//                    fetchDataSource.needToRefresh.tryEmit(true)
                }
            }
            val tracks = fetchDataSource.getAllTracks().take(1).stateIn(this).value
            val ids = (tracks.indices).shuffled().subList(0, 10).map { tracks[it].id }.sortedBy { it }
            Log.i(TAG, "useAppContext: $ids")
            fetchDataSource.getTracksByIds(listOf(3,155,324,2334)).collect {
                Log.i(TAG, "useAppContext: ${it.map { it.id }}")
            }
        }
    }
}