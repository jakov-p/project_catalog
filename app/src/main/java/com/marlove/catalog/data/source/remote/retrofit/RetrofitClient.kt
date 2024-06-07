package com.marlove.catalog.data.source.remote.retrofit

import com.marlove.catalog.data.source.remote.retrofit.utilities.LogJsonInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Qualifier


class RetrofitClient @Inject constructor(@ServerUrl serverUrl: String, @AccessToken accessToken:String)
{
    companion object {
        val SERVER_URL: String = "https://marlove.net/e/mock/v1/"
        val ACCESS_TOKEN: String = "675fa9d079c9f887f359796c1716c52c"
    }

    val service: ICatalogServiceApi

    init
    {
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(AuthorizationInterceptor(accessToken))
            .addInterceptor(LogJsonInterceptor())
            //.addInterceptor(NetworkConnectionInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(ICatalogServiceApi::class.java)
    }

    private class AuthorizationInterceptor(private val accessToken:String) : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {

            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", accessToken)
                .build()
            return chain.proceed(newRequest)
        }
    }

}



@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ServerUrl


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AccessToken