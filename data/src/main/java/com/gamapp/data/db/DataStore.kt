package com.gamapp.data.db

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject



interface ApplicationDatastore {
    val pagerIndex: Flow<Int>
    suspend fun setPagerIndex(index: Int)
}

interface AwaitScope {
    suspend fun await(scope: suspend () -> Unit)
}

suspend inline fun awaitAll(scope: AwaitScope.() -> Unit) {
    val awaitScopeImpl = object : AwaitScope {
        val scopes = mutableListOf<Deferred<Unit>>()
        override suspend fun await(scope: suspend () -> Unit) = coroutineScope {
            scopes += async {
                scope()
            }
        }
    }
    scope.invoke(awaitScopeImpl)
    awaitScopeImpl.scopes.awaitAll()
}


object PlayerDatastore {
    var pagerIndex: Int = 0
        private set
    suspend fun setData(applicationDatastore: ApplicationDatastore) {
        awaitAll {
            await {
                applicationDatastore.pagerIndex.take(1).collect {
                    pagerIndex = it
                }
            }
        }
    }
}

class ApplicationDatastoreImpl @Inject constructor(@ApplicationContext private val context: Context) :
    ApplicationDatastore {
    private val Context.applicationDataStore by preferencesDataStore("ApplicationDataStore")
    object Keys {
        val pagerIndex = intPreferencesKey("pagerIndex")
    }
    override val pagerIndex: Flow<Int> =
        context.applicationDataStore.data.map { it[Keys.pagerIndex] ?: 0 }

    override suspend fun setPagerIndex(index: Int) {
        withContext(Dispatchers.IO) {
            context.applicationDataStore.edit {
                it[Keys.pagerIndex] = index
            }
        }
    }

}
