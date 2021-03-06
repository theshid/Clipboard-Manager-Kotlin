package com.shid.clipboardmanagerkt.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "clip")
data class ClipEntry (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "clip_id")
    var clipId:Int =0,
    var entry:String,
    var date: Date,
    var favorite:Int = 0)