package uz.rezanen.task.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetWalletResponse(
    @SerialName("id")
    val id: Int?,
    @SerialName("phone")
    val phone: String?,
    @SerialName("balance")
    val balance: Int?,
    @SerialName("active_method")
    val activeMethod: String?,
    @SerialName("active_card_id")
    val activeCardId: String?,
)