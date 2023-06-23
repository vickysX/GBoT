package com.example.gbot.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gbot.model.Message
import com.example.gbot.model.Chat

@Database(entities = [Message::class, Chat::class], version = 3)
@TypeConverters(Converters::class)
abstract class GBoTDatabase : RoomDatabase() {
    abstract fun messageDao() : MessageDao

    companion object {
        @Volatile
        private var instance : GBoTDatabase? = null

        fun getDatabase(context : Context) : GBoTDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(context, GBoTDatabase::class.java, "Chat_database")
                    .fallbackToDestructiveMigration()
                    //.addTypeConverter(Converters::class)
                    .build()
                    .also { instance = it }
            }
        }
    }
    // add this method during instantiation
    // RoomDatabase.Builder.addTypeConverter()
}