package uz.rezanen.task.utils

sealed class NetworkResponse<T>(
    val data: T? = null,
    var error: Boolean = false
) {
    class Success<T>(data: T) : NetworkResponse<T>(data = data)
    class Failure<T> : NetworkResponse<T>()
    class Error<T>(val statusCode: Int?=null, val message: String) : NetworkResponse<T>()
    class NoConnection<T> : NetworkResponse<T>()
    class Loading<T>(val isLoading: Boolean) : NetworkResponse<T>()
}