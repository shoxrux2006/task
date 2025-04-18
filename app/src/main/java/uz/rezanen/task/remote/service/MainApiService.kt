package uz.rezanen.task.remote.service

import io.ktor.client.HttpClient
import uz.rezanen.task.remote.request.AddCardRequest
import uz.rezanen.task.remote.request.AddPromoCodeRequest
import uz.rezanen.task.remote.request.AddUserRequest
import uz.rezanen.task.remote.request.SetActiveCardRequest
import uz.rezanen.task.remote.response.AddPromoCodeResponse
import uz.rezanen.task.remote.response.AddUserResponse
import uz.rezanen.task.remote.response.GetCardsResponse
import uz.rezanen.task.remote.response.GetWalletResponse
import uz.rezanen.task.utils.ApiResult
import uz.rezanen.task.utils.RequestType
import uz.rezanen.task.utils.safeRequest

class MainApiService(private val client: HttpClient) {
    suspend fun addUser(addUserRequest: AddUserRequest): ApiResult<AddUserResponse> {
        return safeRequest(RequestType.POST, client, "/users", addUserRequest)
    }

    suspend fun addCard(addCardRequest: AddCardRequest): ApiResult<GetCardsResponse> {
        return safeRequest(RequestType.POST, client, "/cards", addCardRequest)
    }


    suspend fun setPromoCode(addPromoCodeRequest: AddPromoCodeRequest): ApiResult<AddPromoCodeResponse> {
        return safeRequest(RequestType.POST, client, "/promocode", addPromoCodeRequest)
    }

    suspend fun setActiveCard(setActiveCardRequest: SetActiveCardRequest): ApiResult<GetWalletResponse> {
        return safeRequest(RequestType.PUT, client, "/wallet/method", setActiveCardRequest)
    }


    suspend fun getWallet(): ApiResult<GetWalletResponse> {
        return safeRequest(RequestType.GET, client, "/wallet")
    }

    suspend fun getCards(): ApiResult<List<GetCardsResponse>> {
        return safeRequest(RequestType.GET, client, "/cards")
    }

}