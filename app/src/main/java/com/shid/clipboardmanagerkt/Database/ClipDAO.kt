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
    fun loadAllClips(): LiveData<List<ClipEntry?>?>?

    @Insert
    fun insertClip(clipEntry: ClipEntry?)

    @Delete
    fun deleteClip(clipEntry: ClipEntry?)

    @Query("SELECT * FROM clip WHERE favorite = 1 ORDER BY date")
    fun loadFavoriteClips(): LiveData<List<ClipEntry?>?>?

    @Query("UPDATE clip SET favorite = :fav WHERE clipId = :id")
    fun update(fav: Int, id: Int)

    @Query("UPDATE clip SET entry = :ent WHERE clipId = :id")
    fun updateClip(ent: String?, id: Int)
}