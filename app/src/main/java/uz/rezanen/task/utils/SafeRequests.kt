package uz.rezanen.task.utils

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

suspend inline fun <reified T> safeRequest(
    requestType: RequestType, client: HttpClient, url: String, body: Any? = null
): ApiResult<T> {
    return try {
        val response = when (requestType) {
            RequestType.GET -> {
                client.get(url) {
                    body?.let { setBody(it) }

                }
            }

            RequestType.POST -> {
                client.post(url) {
                    body?.let { setBody(it) }
                }
            }

            RequestType.PUT -> {
                client.put(url) {
                    body?.let { setBody(it) }
                }
            }
        }


        if (response.status.isSuccess()) {
            ApiResult.Success(response.body())
        } else {
            ApiResult.Error(response.status.value, response.body(), null)
        }
    } catch (e: ClientRequestException) {
        val errorBody = e.response.bodyAsText()
        ApiResult.Error(-1, errorBody, e)
    } catch (e: ServerResponseException) {
        val errorBody = e.response.bodyAsText()
        ApiResult.Error(-1, errorBody, e)
    } catch (e: Exception) {
        ApiResult.Error(-1, e.message, e)
    }
}

enum class RequestType {
    GET, POST, PUT
}



