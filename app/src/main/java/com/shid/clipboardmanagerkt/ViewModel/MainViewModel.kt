package com.shid.clipboardmanagerkt.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shid.clipboardmanagerkt.Database.ClipDAO
import com.shid.clipboardmanagerkt.Model.ClipEntry
import kotlinx.coroutines.*

class MainViewModel (
     dataSource:ClipDAO,
    application: Application) : AndroidViewModel(application) {

    /**
     * Hold a reference to SleepDatabase via SleepDatabaseDao.
     */
    val database = dataSource

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */
    private var viewModelJob = Job()

    /**
     * A [CoroutineScope] keeps track of all coroutines started by this ViewModel.
     *
     * Because we pass it [viewModelJob], any coroutine started in this uiScope can be cancelled
     * by calling `viewModelJob.cancel()`
     *
     * By default, all coroutines started in uiScope will launch in [Dispatchers.Main] which is
     * the main thread on Android. This is a sensible default because most coroutines started by
     * a [ViewModel] update the UI after performing some processing.
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val allClips = database.loadAllClips()


    fun getAllEntries(){
        viewModelScope.launch {
            val mClipEntries = database.loadAllClips()
        }
    }

     fun deleteClip(selectedClip:ClipEntry){
         uiScope.launch {
             withContext(Dispatchers.IO){
                 database.deleteClip(selectedClip)
             }
         }

    }

    /**
     * Called when the ViewModel is dismantled.
     * At this point, we want to cancel all coroutines;
     * otherwise we end up with processes that have nowhere to return to
     * using memory and resources.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}