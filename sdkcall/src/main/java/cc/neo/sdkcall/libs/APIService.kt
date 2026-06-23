package cc.neo.sdkcall.libs


import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class CallRequest(
    @SerializedName("callerId") val callerId: String,
    @SerializedName("callerName") val callerName: String,
    @SerializedName("callerAvatar") val callerAvatar: String,
    @SerializedName("calleeId") val calleeId: String,
    @SerializedName("calleeName") val calleeName: String,
    @SerializedName("calleeAvatar") val calleeAvatar: String,
    @SerializedName("checkSum") val checkSum: String,
)

data class CallSipRequest(
    @SerializedName("callerId") val callerId: String,
    @SerializedName("callerName") val callerName: String,
    @SerializedName("callerAvatar") val callerAvatar: String,
    @SerializedName("destination") val destination: String,
)

data class CallResponse(
    @SerializedName("token") val token: String,
    @SerializedName("callee") val callee: String,
    @SerializedName("server") val server: String
)

data class Error(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("details") val details: Any? = null
)

sealed class CallResult {
    data class Success(val data: CallResponse): CallResult()
    data class Failure(val error: Error): CallResult()
}

interface ApiService {
    @POST("api/sdk-call/one2one")
    suspend fun requestCall(@Body request: CallRequest): Response<CallResponse>

    @POST("api/sdk-call/app2phone")
    suspend fun requestCallSip(@Body request: CallSipRequest): Response<CallResponse>
}

object ApiClient {
    var BASE_URL = "" // untuk emulator Android
    var AUTH_TOKEN = "" // Ganti dengan token dinamis jika perlu

    val api: ApiService by lazy {
        if (BASE_URL.isBlank()) {
            throw IllegalStateException("BASE_URL cannot be empty.")
        }

        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        val authInterceptor = okhttp3.Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $AUTH_TOKEN")
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }

}

object CallRepository {
    suspend fun requestCall(request: CallRequest): CallResult {
        return try {
            val response = ApiClient.api.requestCall(request)
            if (response.isSuccessful && response.body() != null) {
                CallResult.Success(response.body()!!)
            } else {
                CallResult.Failure(
                    Error(
                        code = response.code(),
                        message = response.errorBody()?.string() ?: "Unknown error"
                    )
                )
            }
        } catch (e: Exception) {
            CallResult.Failure(Error(code = -1, message = e.localizedMessage ?: "Unexpected error"))
        }
    }
    suspend fun requestCallSip(request: CallSipRequest): CallResult {
        return try {
            val response = ApiClient.api.requestCallSip(request)
            if (response.isSuccessful && response.body() != null) {
                CallResult.Success(response.body()!!)
            } else {
                CallResult.Failure(
                    Error(
                        code = response.code(),
                        message = response.errorBody()?.string() ?: "Unknown error"
                    )
                )
            }
        } catch (e: Exception) {
            CallResult.Failure(Error(code = -1, message = e.localizedMessage ?: "Unexpected error"))
        }
    }
}