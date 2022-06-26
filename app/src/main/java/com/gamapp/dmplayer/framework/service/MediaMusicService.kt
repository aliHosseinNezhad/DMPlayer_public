package com.gamapp.dmplayer.framework.service

//private const val TAG = "MediaPlaybackService"
//private const val MY_MEDIA_ROOT_ID = "media_root_id"
//private const val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"
//
//@AndroidEntryPoint
//class MediaPlaybackService : MediaBrowserServiceCompat() {
//    @Inject
//    lateinit var mediaStoreChangeHandler: MediaStoreChangeHandler
//
//    @Inject
//    lateinit var applicationDataStore: ApplicationDatastore
//
//    private var mediaSession: MediaSessionCompat? = null
//    private lateinit var stateBuilder: PlaybackStateCompat.Builder
//
//    private var musicPlayerHandlerThread: HandlerThread? = null
//    private var playerHandler: Handler? = null
//    private var mediaCategoryObserver: MediaCategoryObserver? = null
//    private lateinit var playerView: RemoteViews
//
//    val binder = MediaBinder()
//
//    inner class MediaBinder : Binder() {
//        fun getService(): MediaPlaybackService = this@MediaPlaybackService
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return binder
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        setMediaSession()
//        playerView = RemoteViews(packageName, R.layout.music_notification)
//        musicPlayerHandlerThread = HandlerThread("PlaybackHandler")
//        musicPlayerHandlerThread?.start()
//        playerHandler = Handler(musicPlayerHandlerThread!!.looper)
////        mediaCategoryObserver = MediaCategoryObserver(this, playerHandler!!)
//        registerCategoryObserver(
//            contentResolver = contentResolver,
//            mediaCategoryObserver = mediaCategoryObserver!!
//        )
//        val intents = Intent(applicationContext, MainActivity::class.java)
//        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
//            addNextIntentWithParentStack(intents)
//            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
//        }
//        val notification: Notification = createNotification(pendingIntent)
//        startForeground(Constant.MUSIC_PLAYER_NOTIFICATION_ID, notification)
//    }
//
//    private fun setMediaSession() {
////        mediaSession = MediaSessionCompat(baseContext, TAG).apply {
////
////            // Enable callbacks from MediaButtons and TransportControls
////            setFlags(
////                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
////                        or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
////            )
////
////            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
////            stateBuilder = PlaybackStateCompat.Builder()
////                .setActions(
////                    PlaybackStateCompat.ACTION_PLAY
////                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
////                )
////            setPlaybackState(stateBuilder.build())
////
////            // MySessionCallback() has methods that handle callbacks from a media controller
////            setCallback()
////
////            // Set the session's token so that client activities can communicate with it.
////            setSessionToken(sessionToken)
////        }
//
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        super.onStartCommand(intent, flags, startId)
//        return START_STICKY
//    }
//
//    private fun createNotification(pendingIntent: PendingIntent): Notification {
//
//
//        return NotificationCompat.Builder(applicationContext, Constant.NOTIFICATION_CHANNEL_ID)
//            .setContentIntent(pendingIntent)
//            .setColor(primary.toArgb())
//            .setColorized(true)
//            .setContentTitle("MusicName")
//            .setContentText("music player is playing music")
//            .setSmallIcon(R.drawable.ic_music_note)
//            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .apply {
////                val action = NotificationCompat.Action(
////                    R.drawable.round_pause_24,
////                    getString(R.string.pause),
////                    MediaButtonReceiver.buildMediaButtonPendingIntent(
////                        applicationContext,
////                        PlaybackStateCompat.ACTION_PLAY_PAUSE
////                    )
////                )
////                addAction(
////                    action
////                )
//            }
//            .setCustomContentView(playerView)
//            .build()
//    }
//
//    override fun onGetRoot(
//        clientPackageName: String,
//        clientUid: Int,
//        rootHints: Bundle?,
//    ): BrowserRoot {
//        return BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null)
//    }
//
//    override fun onLoadChildren(
//        parentId: String,
//        result: Result<MutableList<MediaBrowserCompat.MediaItem>>,
//    ) {
//        if (MY_EMPTY_MEDIA_ROOT_ID == parentId) {
//            result.sendResult(null)
//            return
//        }
//
//        // Assume for example that the music catalog is already loaded/cached.
//
//        val mediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()
//
//        // Check if this is the root menu:
//        if (MY_MEDIA_ROOT_ID == parentId) {
//            // Build the MediaItem objects for the top level,
//            // and put them in the mediaItems list...
//        } else {
//            // Examine the passed parentId to see which submenu we're at,
//            // and put the children of that menu in the mediaItems list...
//        }
//        result.sendResult(mediaItems)
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        contentResolver.unregisterContentObserver(mediaCategoryObserver!!)
//        playerHandler?.removeCallbacksAndMessages(null)
//        musicPlayerHandlerThread?.quitSafely()
//    }
//
//    fun notifyChange(what: String) {
//        if (what == ACTIONS.MEDIA_STORE_CHANGED) {
//            sendBroadcast(Intent(what))
//            mediaStoreChangeHandler.notifyMediaStoreChanged()
//        }
//    }
//}
