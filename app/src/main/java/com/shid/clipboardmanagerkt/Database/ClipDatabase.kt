package com.shid.clipboardmanagerkt.Database

import android.content.Context
import androidx.room.*
import com.shid.clipboardmanagerkt.Model.ClipEntry
import java.security.AccessControlContext

@Database(entities = [ClipEntry::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class ClipDatabase :RoomDatabase(){

    abstract val clipDao: ClipDAO

    companion object {
        @Volatile
        private var INSTANCE: ClipDatabase? = null

        fun getInstance(context: Context): ClipDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ClipDatabase::class.java,
                        "clip_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}