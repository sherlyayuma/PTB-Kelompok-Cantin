package com.example.cantin.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cantin.data.local.dao.CartDao
import com.example.cantin.data.local.entity.CartEntity
import com.example.cantin.data.local.dao.FavoriteDao
import com.example.cantin.data.local.dao.ReviewDao
import com.example.cantin.data.local.entity.ReviewEntity
import com.example.cantin.data.local.entity.FavoriteEntity

@Database(entities = [CartEntity::class, FavoriteEntity::class, ReviewEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cantin_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
