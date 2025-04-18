package uz.rezanen.task.domain.impl

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import uz.rezanen.task.domain.WalletRepository
import uz.rezanen.task.remote.response.GetWalletResponse
import uz.rezanen.task.remote.service.MainApiService
import uz.rezanen.task.utils.ApiResult
import uz.rezanen.task.utils.NetworkResponse
import uz.rezanen.task.utils.hasConnection

class WalletRepositoryImpl(
    private val context: Context,
    private val client: MainApiService,
) : WalletRepository {
    override suspend fun getWallet(): Flow<NetworkResponse<GetWalletResponse>> =
        flow<NetworkResponse<GetWalletResponse>> {
            if (hasConnection(context)) {
                emit(NetworkResponse.Loading(true))
                val request = client.getWallet(

                )
                when (request) {
                    is ApiResult.Error -> {
                        emit(
                            NetworkResponse.Error(
                                message = request.message ?: "Check Your Code"
                            )
                        )
                    }

                    is ApiResult.Success -> {
                        emit(NetworkResponse.Success(request.data))
                    }
                }
            } else {
                emit(NetworkResponse.NoConnection())
            }
        }.flowOn(Dispatchers.IO)
}