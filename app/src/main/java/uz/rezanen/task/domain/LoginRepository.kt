package uz.rezanen.task.domain

import kotlinx.coroutines.flow.Flow
import uz.rezanen.task.remote.request.AddUserRequest
import uz.rezanen.task.remote.response.AddUserResponse
import uz.rezanen.task.utils.NetworkResponse

interface LoginRepository {
    suspend fun addUser(addUserRequest: AddUserRequest): Flow<NetworkResponse<AddUserResponse>>
    fun setPhoneNumber(phone: String)
    fun getPhoneNumber(): String?
    fun clearPhoneNumber()

}