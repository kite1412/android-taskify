package com.nrr.data.repository

import com.nrr.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>

    suspend fun setUsername(newUsername: String)
}