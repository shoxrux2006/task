package uz.rezanen.task.domain.impl

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import uz.rezanen.task.domain.LoginRepository
import uz.rezanen.task.remote.providers.PhoneNumberProvider
import uz.rezanen.task.remote.request.AddUserRequest
import uz.rezanen.task.remote.response.AddUserResponse
import uz.rezanen.task.remote.service.MainApiService
import uz.rezanen.task.utils.ApiResult
import uz.rezanen.task.utils.NetworkResponse
import uz.rezanen.task.utils.hasConnection

class LoginRepositoryImpl(
    private val context: Context,
    private val client: MainApiService,
    private val phoneNumberProvider: PhoneNumberProvider
) : LoginRepository {
    override suspend fun addUser(addUserRequest: AddUserRequest): Flow<NetworkResponse<AddUserResponse>> =
        flow<NetworkResponse<AddUserResponse>> {
            if (hasConnection(context)) {
                emit(NetworkResponse.Loading(true))
                val request = client.addUser(
                    addUserRequest
                )
                when (request) {
                    is ApiResult.Error -> {
                        if (request.code == 409) {
                            emit(
                                NetworkResponse.Error(
                                    statusCode = request.code,
                                    message = request.message ?: "Check Your Code"
                                )
                            )
                        } else {
                            emit(
                                NetworkResponse.Error(
                                    message = request.message ?: "Check Your Code"
                                )
                            )
                        }
                    }

                    is ApiResult.Success -> {
                        emit(NetworkResponse.Success(request.data))
                    }
                }
            } else {
                emit(NetworkResponse.NoConnection())
            }
        }.flowOn(Dispatchers.IO)

    override fun setPhoneNumber(phone: String) {
        phoneNumberProvider.setPhone(phone)
    }

    override fun getPhoneNumber(): String? {
       return phoneNumberProvider.getPhone()
    }

    override fun clearPhoneNumber() {
        phoneNumberProvider.clearPhone()
    }
}