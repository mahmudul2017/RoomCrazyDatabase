package com.room.crazydatabase.roomDB.db

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.room.crazydatabase.roomDB.dao.ImageDao
import com.room.crazydatabase.roomDB.model.ImageModel

@Database(entities = [ImageModel::class], version = 1, exportSchema = false)
abstract class ImageDatabase: RoomDatabase() {
    abstract fun imageDao(): ImageDao

    companion object {
        @Volatile
        private var INSTANCE: ImageDatabase? = null

        /*private val migration_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE HiltLogIn ADD COLUMN userpic Bitmap DEFAULT ''")
            }
        }*/

        fun getDataBaseClient(context: Context): ImageDatabase {
            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {
                INSTANCE = Room
                    .databaseBuilder(context, ImageDatabase::class.java, "IMAGE_DATABASE")
                    //.fallbackToDestructiveMigration()
                    //.addMigrations(migration_1_2)
                    .allowMainThreadQueries()
                    .build()

                return INSTANCE!!
            }
        }
    }
}