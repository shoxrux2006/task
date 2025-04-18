package uz.rezanen.task.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddCardRequest(
    @SerialName("number")
    val number: String?,
    @SerialName("expire_date")
    val expireDate: String?,
)