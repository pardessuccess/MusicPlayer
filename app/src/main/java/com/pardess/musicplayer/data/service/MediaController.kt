package com.pardess.musicplayer.data.service

import androidx.media3.session.MediaController
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
interface MediaControllerManager {
    val mediaControllerFlow: Flow<MediaController>
}

@Singleton
class ConnectedMediaController @Inject constructor(
    mediaControllerFuture: ListenableFuture<MediaController>,
) : MediaControllerManager {

    override val mediaControllerFlow: Flow<MediaController> = callbackFlow {
        Futures.addCallback(
            mediaControllerFuture,
            object : FutureCallback<MediaController> {
                override fun onSuccess(result: MediaController) {
                    trySend(result)
                }

                override fun onFailure(t: Throwable) {
                    cancel(CancellationException(t.message))
                }
            },
            MoreExecutors.directExecutor()
        )

        awaitClose { }
    }
}