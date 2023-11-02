package com.example.campusolx.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time

@Entity(tableName = "auth_token")
data class AuthToken(
    @PrimaryKey val id: Int = 1,
    val token: String?,
    val expirationTime: Time,
    val userId: String?,
    val branch: String?,
    val enrollmentNo: String?,
    val semester: Int?,
    val contact: String?,
    val upiId: String?,
    val emailId: String?
)