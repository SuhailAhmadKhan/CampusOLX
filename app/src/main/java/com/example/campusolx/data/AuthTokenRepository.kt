package com.example.campusolx.data

import java.sql.Time

class AuthTokenRepository(private val authTokenDao: AuthTokenDao) {
    suspend fun saveAuthToken(
        token: String,
        expirationTime: Time,
        userId: String?,
        branch: String?,
        enrollmentNo: String?,
        semester: Int?,
        contact: String?,
        upiId: String?,
        emailId: String?
    ) {
        val authToken = AuthToken(
            token = token,
            expirationTime = expirationTime,
            userId = userId,
            branch = branch,
            enrollmentNo = enrollmentNo,
            semester = semester,
            contact = contact,
            upiId = upiId,
            emailId = emailId
        )
        authTokenDao.insertAuthToken(authToken)
    }

    suspend fun getAuthToken(): AuthToken? {
        return authTokenDao.getAuthToken()
    }

    suspend fun getUserId(): String? {
        return authTokenDao.getUserId()
    }

    suspend fun deleteAuthToken(authToken: AuthToken) {
        authTokenDao.deleteAuthToken(authToken)
    }
}