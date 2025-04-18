package uz.rezanen.task.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddUserRequest(
    @SerialName("phone")
    val phone: String?,
)