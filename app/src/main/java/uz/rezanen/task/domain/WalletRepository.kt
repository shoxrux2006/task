package uz.rezanen.task.domain

import kotlinx.coroutines.flow.Flow
import uz.rezanen.task.remote.response.GetWalletResponse
import uz.rezanen.task.utils.NetworkResponse

interface WalletRepository {
    suspend fun getWallet(): Flow<NetworkResponse<GetWalletResponse>>
}