package uz.rezanen.task.remote.providers

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import uz.rezanen.task.utils.Const

class KtorClientProvider(
    private val phoneNumberProvider: PhoneNumberProvider
) {
    fun provide(): HttpClient {
        return  HttpClient(OkHttp) {
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = Const.BaseURL
                }
                phoneNumberProvider.getPhone()?.let {number->
                    header("X-Account-Phone",number)
                }

                header("Content-Type", "application/json")
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("DDD", message) // Android Logcat
                    }
                }
                level = LogLevel.ALL
            }
        }
    }
}