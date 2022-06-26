package com.gamapp.dmplayer.presenter.ui.screen

//
//@Composable
//fun App() {
//    val viewModel = hiltViewModel<AppViewModel>()
//    val background = if (isSystemInDarkTheme()) Color.Black else light
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(background)
//    ) {
//        Box(modifier = Modifier
//            .fillMaxSize()
//            .padding(
//                top = viewModel.statusBarHeight,
//                bottom = viewModel.navigationBarHeight + 30.dp
//            )){
//            AppNavigation()
//        }
//        TrackPlayerScreen()
//    }
//}
//
//fun NavGraphBuilder.trackBy(){
//    composable(route = Routes.TracksByQueue.name + "/{data}", arguments = listOf(
//        navArgument(name = "data") {
//            this.type = NavType.StringType
//        }
//    )) {
//        val data = it.arguments?.getString("data") ?: return@composable
//        val gson = Gson()
//        val queueModel = gson.fromJson(data, QueueModel::class.java)
////        TracksByQueue(queueModel = queueModel)
//    }
//    composable(route = Routes.TracksByAlbum.name + "/{data}", arguments = listOf(
//        navArgument(name = "data") {
//            type = NavType.StringType
//        }
//    )) {
//        val gson = Gson()
////        val albumEntity = gson.fromJson(it.arguments?.getString("data") ?: return@composable,
////            AlbumEntity::class.java)
////        TracksByAlbum(albumEntity = albumEntity)
//    }
//    composable(route = Routes.TracksByArtis.name + "/{data}", arguments = listOf(
//        navArgument(name = "data") {
//            type = NavType.StringType
//        }
//    )) {
//        val gson = Gson()
////        val artistEntity = gson.fromJson(it.arguments?.getString("data") ?: return@composable,
////            ArtistEntity::class.java)
////        TracksByArtist(artistEntity = artistEntity)
//    }
//    composable(route = Routes.TracksByFavorite.name) {
//        TracksByFavorite()
//    }
//}
//@Composable
//fun AppNavigation() {
//    val navController = rememberNavController()
//    NavHost(navController = navController, startDestination = Routes.Albums.name) {
//        composable(route = Routes.Albums.name) {
////            Main(navController)
//        }
//        trackBy()
//    }
//}


