package com.af.pb.data.repositories

import com.af.network.model.BaseResponse
import com.af.pb.data.sources.RemoteDataSource
import retrofit2.Response
import javax.inject.Inject

class RemoteRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {

//    suspend fun login(request: LoginRequest): Response<BaseResponse<LoginData>> =
//        remoteDataSource.login(request)

}
