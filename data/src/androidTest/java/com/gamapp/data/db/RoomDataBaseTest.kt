package com.gamapp.data.db

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.gamapp.data.dao.MusicDao
import com.gamapp.data.dao.QueueDao
import com.gamapp.data.data_source.MusicRoomDatabase
import com.gamapp.data.entity.QueueEntity
import com.gamapp.data.entity.TrackEntity
import com.gamapp.data.mapper.toTrackModel
import com.gamapp.data.repository.FavoriteRepositoryImp
import com.gamapp.data.repository.QueueRepositoryImp
import com.gamapp.data.utils.DefaultQueueIds
import com.gamapp.data.utils.UniqueIdManager
import com.gamapp.domain.repository.QueueRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

const val TAG = "RoomDatabaseTest"

@RunWith(AndroidJUnit4::class)
@SmallTest
class RoomDataBaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: MusicRoomDatabase
    private lateinit var musicDao: MusicDao
    private lateinit var queueDao: QueueDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room
            .databaseBuilder(context, MusicRoomDatabase::class.java,"music_database_test")
//            .inMemoryDatabaseBuilder(context, MusicRoomDatabase::class.java)
            .allowMainThreadQueries().build()
        db.clearAllTables()
        musicDao = db.musicDao
        queueDao = db.queueDao
        runTest {
            insertDefaultQueues()
        }
    }


    suspend fun insertDefaultQueues() {
        queueDao.insertWithIgnore(
            QueueEntity(
                DefaultQueueIds.Favorite,
                defaultQueue = 1,
                title = DefaultQueueIds.Favorite
            )
        )
        queueDao.insertWithIgnore(
            QueueEntity(
                DefaultQueueIds.Queue,
                defaultQueue = 1,
                title = DefaultQueueIds.Queue
            )
        )
    }

    @After
    fun teardown() {
        db.close()
    }

    private suspend fun logQueueAndTracks(
        message: String,
        queueRepository: QueueRepository,
    ) {
        val queues = queueDao.getAllQueues()
        Log.i(
            TAG,
            "$message ${queues[2].title} tracks: ${
                queueRepository.getAllMusicByQueueId(
                    queueId = queues[2].id
                ).map { it.title }.joinToString { it }
            }"
        )
    }


    @Test
    fun test2() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val queueRepository = QueueRepositoryImp(queueDao, musicDao, UniqueIdManager(context))
        val favoriteRepository = FavoriteRepositoryImp(queueRepository, queueDao)
        val track1 = TrackEntity(2, "name1", "title1", "artist", 0, 0, -1, -1, "album", 0L)
        val track2 = TrackEntity(3, "name2", "title2", "artist", 0, 0, -1, -1, "album", 0L)
        val track3 = TrackEntity(5, "name3", "title3", "artist", 0, 0, -1, -1, "album", 0L)
        val track4 = TrackEntity(6, "name4", "title4", "artist", 0, 0, -1, -1, "album", 0L)

        musicDao.insert(
            listOf(
                track1,
                track2,
                track3,
                track4
            )
        )

        queueRepository.createQueue("Favor")
        queueRepository.createQueue("Nice")
        val queues = queueDao.getAllQueues().filter { it.defaultQueue == 0 }
        val queue1 = queues[0]
        val queue2 = queues[1]
        queueRepository.addTrackToQueue(queue1.id, track1.toTrackModel())
        queueRepository.addTrackToQueue(queue2.id, track1.toTrackModel())
        queueRepository.addTrackToQueue(queue1.id, track2.toTrackModel())
        queueRepository.addTrackToQueue(queue2.id, track2.toTrackModel())
//        musicDao.insertWithIgnore(track1)
//        queueDao.insertQueueTrackCrossRef(QueueTrackCrossRef(queueId = queue2.id, trackId = track1.fileId))
        launch {
            queueDao.getTracksOfQueueViaFlow(queue1.id).collect {
                Log.i(
                    TAG,
                    "1-> queue:${it.queue.title} tracks:${
                        it.tracks.map { it.title }.joinToString { it }
                    }"
                )
            }
        }
        launch {
            queueDao.getTracksOfQueueViaFlow(queue2.id).collect {
                Log.i(
                    TAG,
                    "2-> queue:${it.queue.title} tracks:${
                        it.tracks.map { it.title }.joinToString { it }
                    }"
                )
            }
        }
        launch {
            queueDao.getAllQueuesViaFlow().collect {
                Log.i(TAG, "queues:${it.map { it.title }.joinToString { it }}")
            }
        }

    }

    @Test
    fun createDefaultEntity() = runTest {

    }
}