package com.example.campusolx.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AuthTokenDao {
    @Query("SELECT * FROM auth_token LIMIT 1")
    suspend fun getAuthToken(): AuthToken?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthToken(authToken: AuthToken)

    @Delete
    suspend fun deleteAuthToken(authToken: AuthToken)

    @Query("SELECT userId FROM auth_token LIMIT 1")
    suspend fun getUserId(): String?
}