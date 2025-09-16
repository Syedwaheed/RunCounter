package com.edu.auth.domain

import com.edu.core.domain.util.DataError
import com.edu.core.domain.util.EmptyResult
import com.edu.core.domain.util.Result
import javax.xml.crypto.Data

interface AuthRepository{
    suspend fun register(email: String, password: String): EmptyResult<DataError.Network>
    suspend fun login(email:String,password:String): EmptyResult<DataError.Network>
}