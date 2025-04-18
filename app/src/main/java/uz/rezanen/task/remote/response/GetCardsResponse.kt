package uz.rezanen.task.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetCardsResponse(
    @SerialName("id")
    val id: Int?,
    @SerialName("number")
    val number: String?,
    @SerialName("expire_date")
    val expireDate: String?,
    @SerialName("user_id")
    val userId: Int?
)