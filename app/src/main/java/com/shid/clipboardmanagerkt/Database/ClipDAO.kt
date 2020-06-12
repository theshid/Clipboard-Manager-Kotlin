package com.shid.clipboardmanagerkt.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.shid.clipboardmanagerkt.Model.ClipEntry

@Dao
interface ClipDAO {

    @Query("SELECT * FROM clip ORDER BY date")
    suspend fun loadAllClips(): LiveData<List<ClipEntry?>?>?

    @Insert
    suspend fun insertClip(clipEntry: ClipEntry?)

    @Delete
    suspend fun deleteClip(clipEntry: ClipEntry?)

    @Query("SELECT * FROM clip WHERE favorite = 1 ORDER BY date")
    suspend fun loadFavoriteClips(): LiveData<List<ClipEntry?>?>?

    @Query("UPDATE clip SET favorite = :fav WHERE clipId = :id")
    suspend fun update(fav: Int, id: Int)

    @Query("UPDATE clip SET entry = :ent WHERE clipId = :id")
    suspend fun updateClip(ent: String?, id: Int)
}