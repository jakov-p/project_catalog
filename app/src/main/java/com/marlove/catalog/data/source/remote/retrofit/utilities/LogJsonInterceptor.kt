package com.marlove.catalog.data.source.remote.retrofit.utilities

import com.marlove.catalog.logger.log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException

/**
 * Just logs the JSON content (with indentations)
 */
class LogJsonInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response     {

        val request: Request = chain.request()
        val response: Response = chain.proceed(request)
        val rawJson = response.body?.string() ?: ""

        try {
            log.d("requestURL = $request.url")
            val obj = JSONTokener(rawJson).nextValue()
            val jsonLog = if (obj is JSONObject) {
                obj.toString(4)
            }
            else {
                (obj as JSONArray?)?.toString(4)
            }

            jsonLog?.let{log.d(it)}
        }
        catch (e: Exception) {
            log.e("",e)
        }

        // Recreate the response before it is returned, because the body can be read only once
        val responseBody = ResponseBody.create(response.body?.contentType(), rawJson)
        return response.newBuilder().body(responseBody).build()
    }
}