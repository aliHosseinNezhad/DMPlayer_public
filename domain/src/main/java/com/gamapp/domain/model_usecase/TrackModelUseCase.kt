package com.gamapp.domain.model_usecase

import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.models.QueueModel
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.usecase.interacts.Interacts


suspend fun TrackModel.remove(act: Interacts) {
    act.track.remove.invoke(this)
}

suspend fun List<TrackModel>.remove(act: Interacts) {
    act.track.remove.invoke(this)
}

//suspend fun TrackModel.update(act: Interacts) {
//    act.track.update.invoke(this)
//}

suspend fun AlbumModel.remove(act: Interacts) {
    act.album.remove.invoke(listOf(this))
}
@JvmName("removeAlbumModels")
suspend fun List<AlbumModel>.remove(act:Interacts){
    act.album.remove.invoke(this)
}

suspend fun ArtistModel.remove(act:Interacts){
    act.artist.remove.invoke(listOf(this))
}

@JvmName("removeArtistModels")
suspend fun List<ArtistModel>.remove(act: Interacts){
    act.artist.remove.invoke(this)
}

suspend fun QueueModel.remove(act: Interacts){
    act.queue.removeQueue.invoke(this)
}

@JvmName("removeQueueModels")
suspend fun List<QueueModel>.remove(act: Interacts){
    act.queue.removeQueue.invoke(this)
}


