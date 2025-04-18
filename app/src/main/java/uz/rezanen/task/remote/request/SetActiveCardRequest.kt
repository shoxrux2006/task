package uz.rezanen.task.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SetActiveCardRequest(
    @SerialName("active_method")
    val activeMethod: String?,
    @SerialName("active_card_id")
    val activeCardId: Int?,
)