package uz.rezanen.task.utils

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int, val message: String?, val exception: Throwable?) : ApiResult<Nothing>()
}