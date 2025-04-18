package uz.rezanen.task.domain.impl

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import uz.rezanen.task.domain.CardRepository
import uz.rezanen.task.remote.request.AddCardRequest
import uz.rezanen.task.remote.request.AddPromoCodeRequest
import uz.rezanen.task.remote.request.SetActiveCardRequest
import uz.rezanen.task.remote.response.AddPromoCodeResponse
import uz.rezanen.task.remote.response.GetCardsResponse
import uz.rezanen.task.remote.response.GetWalletResponse
import uz.rezanen.task.remote.service.MainApiService
import uz.rezanen.task.utils.ApiResult
import uz.rezanen.task.utils.NetworkResponse
import uz.rezanen.task.utils.hasConnection

class CardRepositoryImpl(
    private val context: Context,
    private val client: MainApiService,

    ) : CardRepository {
    override suspend fun addCard(addCardRequest: AddCardRequest): Flow<NetworkResponse<GetCardsResponse>> =
        flow<NetworkResponse<GetCardsResponse>> {
            if (hasConnection(context)) {
                emit(NetworkResponse.Loading(true))
                val request = client.addCard(
                    addCardRequest
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

    override suspend fun setPromoCode(addPromoCodeRequest: AddPromoCodeRequest): Flow<NetworkResponse<AddPromoCodeResponse>> =
        flow<NetworkResponse<AddPromoCodeResponse>> {
            if (hasConnection(context)) {
                emit(NetworkResponse.Loading(true))
                val request = client.setPromoCode(
                    addPromoCodeRequest
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

    override suspend fun setActiveCard(setActiveCardRequest: SetActiveCardRequest): Flow<NetworkResponse<GetWalletResponse>> =
        flow<NetworkResponse<GetWalletResponse>> {
            if (hasConnection(context)) {
                emit(NetworkResponse.Loading(true))
                val request = client.setActiveCard(
                    setActiveCardRequest
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

    override suspend fun getCards(): Flow<NetworkResponse<List<GetCardsResponse>>> =
        flow<NetworkResponse<List<GetCardsResponse>>> {
            if (hasConnection(context)) {
                emit(NetworkResponse.Loading(true))
                val request = client.getCards(

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