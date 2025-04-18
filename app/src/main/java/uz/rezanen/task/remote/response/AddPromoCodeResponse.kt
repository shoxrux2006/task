package uz.rezanen.task.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddPromoCodeResponse(
    @SerialName("message")
    val message: String?,
)