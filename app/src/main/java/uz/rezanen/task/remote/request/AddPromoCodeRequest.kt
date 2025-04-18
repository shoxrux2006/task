package uz.rezanen.task.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddPromoCodeRequest(
    @SerialName("code")
    val code: String?,
)