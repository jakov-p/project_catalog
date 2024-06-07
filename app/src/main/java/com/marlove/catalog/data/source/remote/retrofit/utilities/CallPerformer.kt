package com.marlove.catalog.data.source.remote.retrofit.utilities

import com.marlove.catalog.logger.log
import com.marlove.catalog.logger.printTitle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response
import kotlin.system.measureTimeMillis

/**
 * Call performer is a wrapper that performs a http request and logs
 * the result (it also measures the time).
 *
 * @param T the payload data of the response
 * @param commandDescription the describing text, just for logging
 * @param methodCall the actual HTTP request to be performed
 */

class CallPerformer<T>(val commandDescription :String,
                       val methodCall: suspend ()-> Response<T>) {

    suspend fun perform():T =
        try {
            printTitle(commandDescription)

            delay(2000) //Just for testing //TODO remove this

            val response:Response<T>
            measureTimeMillis  {
                response = methodCall.invoke()
            }.also {
                log.i("The execution time  = ${it.toFloat() / 1000}")
            }

            if (response.isSuccessful)
            {
                log.i( response.message())
                val data: T? = response.body()
                val code = response.code()

                log.i("response = $response")
                log.i("code = $code")
                log.i("body = ${data?.toString()}")

                data?: throw Exception("Request failed, empty body returned")
            }
            else
            {
                log.e( "$commandDescription failed")
                log.e( "response = $response")
                log.e("code = ${response.code()}")

                val errorBody = response.errorBody()?.string() ?:""
                log.e("body = $errorBody")
                throw Exception("Request failed, errorCode = ${response.code()}")
            }
        }
        finally {
            log.i( "****************************************************************************** ")
        }
}
