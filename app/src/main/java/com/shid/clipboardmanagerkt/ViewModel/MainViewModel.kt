package com.shid.clipboardmanagerkt.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.shid.clipboardmanagerkt.Database.ClipDAO

class MainViewModel (
    val database:ClipDAO,
    application: Application) : AndroidViewModel(application) {
}