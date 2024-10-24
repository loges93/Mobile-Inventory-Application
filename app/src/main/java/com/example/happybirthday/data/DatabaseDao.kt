package com.example.happybirthday.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import com.example.happybirthday.data.User
import com.example.happybirthday.data.Item

@Dao
interface UserDao {

    // Insert a new user into the users table
    @Insert
    suspend fun insertUser(user: User)

    // Insert multiple users
    @Insert
    suspend fun insertUsers(users: List<User>)

    // Update an existing user
    @Update
    suspend fun updateUser(user: User)

    // Delete a specific user
    @Delete
    suspend fun deleteUser(user: User)

    // Query to get a user by their ID
    @Query("SELECT * FROM users WHERE userId = :id")
    suspend fun getUserById(id: Int): User?

    // Query to get all users
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    // Query to check if a user with given firstName, lastName, and password exists
    @Query("SELECT * FROM users WHERE firstName = :firstName AND lastName = :lastName AND passWord = :passWord")
    suspend fun getUserByCredentials(firstName: String, lastName: String, passWord: String): User?

    // Query to get a user by their username
    @Query("SELECT * FROM users WHERE firstName = :username")
    suspend fun getUserByUsername(username: String): User?
}


@Dao
interface ItemDao {

    // Insert a new item into the items table
    @Insert
    suspend fun insertItem(item: Item)

    // Insert multiple items
    @Insert
    suspend fun insertItems(items: List<Item>)

    // Update an existing item in the items table
    @Update
    suspend fun updateItem(item: Item)

    // Delete a specific item from the items table
    @Delete
    suspend fun deleteItem(item: Item)

    // Delete all items from the items table
    @Query("DELETE FROM items")
    suspend fun deleteAllItems()

    // Query to get an item by its ID
    @Query("SELECT * FROM items WHERE itemId = :id")
    suspend fun getItemById(id: Int): Item?

    // Query to get all items
    @Query("SELECT * FROM items")
    suspend fun getAllItems(): List<Item>


    // Delete an item by its ID
    @Query("DELETE FROM items WHERE itemId = :id")
    suspend fun deleteItemById(id: Int)

    //Get items that are out of stock
    @Query("SELECT * FROM items WHERE quantity = 0")
    suspend fun get_outofstock_items(): List<Item>
}
