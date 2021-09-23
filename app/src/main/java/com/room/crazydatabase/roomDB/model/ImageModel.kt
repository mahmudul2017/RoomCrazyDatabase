package com.room.crazydatabase.roomDB.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ImageUpload")
data class ImageModel(
    @ColumnInfo(name = "username")
    var userName: String?,

    @ColumnInfo(name = "userpic", typeAffinity = ColumnInfo.BLOB)
    var userPic: ByteArray?
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null
}