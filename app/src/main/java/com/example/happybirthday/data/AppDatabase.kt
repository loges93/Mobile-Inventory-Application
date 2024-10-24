package com.example.happybirthday.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.happybirthday.data.Item
import com.example.happybirthday.data.ItemDao
import com.example.happybirthday.data.User
import com.example.happybirthday.data.UserDao

@Database(entities = [User::class, Item::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Define DAOs as abstract methods
    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao

    companion object {
        // Volatile ensures that the value of INSTANCE is always up-to-date and the same to all execution threads.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Function to get the database instance
        fun getDatabase(context: Context): AppDatabase {
            // If the INSTANCE is null, create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    // Option 2: Fallback to destructive migration during development
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
