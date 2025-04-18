package uz.rezanen.task.domain

import kotlinx.coroutines.flow.Flow
import uz.rezanen.task.remote.request.AddCardRequest
import uz.rezanen.task.remote.request.AddPromoCodeRequest
import uz.rezanen.task.remote.request.SetActiveCardRequest
import uz.rezanen.task.remote.response.AddPromoCodeResponse
import uz.rezanen.task.remote.response.GetCardsResponse
import uz.rezanen.task.remote.response.GetWalletResponse
import uz.rezanen.task.utils.NetworkResponse

interface CardRepository {
    suspend fun addCard(addCardRequest: AddCardRequest): Flow<NetworkResponse<GetCardsResponse>>
    suspend fun setPromoCode(addPromoCodeRequest: AddPromoCodeRequest): Flow<NetworkResponse<AddPromoCodeResponse>>
    suspend fun setActiveCard(setActiveCardRequest: SetActiveCardRequest): Flow<NetworkResponse<GetWalletResponse>>
    suspend fun getCards(): Flow<NetworkResponse<List<GetCardsResponse>>>
}