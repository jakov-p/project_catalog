package com.marlove.catalog.data.source.remote.retrofit.utilities

import com.marlove.catalog.logger.log
import kotlinx.coroutines.delay
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Downloads a file. Supports cancellation.
 */
class FileDownloader(private val url: String): IFileDownloader {

    private val okHttpClient =
         OkHttpClient().newBuilder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        //.addInterceptor(NetworkConnectionInterceptor())
        .build()

    private var call:Call? = null

    @Throws(IOException::class)
    override fun download(): ByteArray     {

        log.d("Downloading a file, file = ${url}")

        val request: Request = Request.Builder().url(url).build()
        val response = okHttpClient.newCall(request).let{
            call = it
            it.execute()
        }

        if(response.isSuccessful) {
            val responseBody = response.body
                ?: throw Exception("Response doesn't contain a file, url = $url")

            return responseBody.byteStream().readBytes().also{
                log.d("File's size is ${it.size} bytes")
            }
        }
        else {
            throw Exception("Request failed, errorCode = ${response.code}")
        }
    }

    override fun cancel(){
        call?.cancel()
    }
}

fun interface IFileDownloaderFactory {
    fun createFileDownloader(url: String): IFileDownloader
}


interface IFileDownloader {
    fun download(): ByteArray
    fun cancel()
}
