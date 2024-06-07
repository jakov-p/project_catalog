package com.marlove.catalog.data.source.remote

import com.marlove.catalog.data.repository.IRemoteCatalogSource
import com.marlove.catalog.data.source.remote.retrofit.CatalogServiceApi
import com.marlove.catalog.data.source.remote.retrofit.RetrofitClient
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.File
import java.net.HttpURLConnection


@RunWith(MockitoJUnitRunner::class)
class RetrofitServiceTest {

    private var mockWebServer = MockWebServer()
    private lateinit var remoteCatalogSource : IRemoteCatalogSource

    @Before
    fun setup() {

        mockWebServer.start()
        val serverUrl = mockWebServer.url("/").toString()
        remoteCatalogSource = RemoteCatalogSource(CatalogServiceApi(RetrofitClient(serverUrl, "12345")))
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testItemsFetching()  = runTest {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(readFromFile("catalogItems.json"))

        mockWebServer.enqueue(response)// Act

        val catalogItems = remoteCatalogSource.getItemsMaxId(null)

        assertEquals(10, catalogItems.size)
        with(catalogItems.get(0))
        {
            assertEquals("664f410e99861", id)
            assertEquals("20. gupfk", text)
            assertEquals(0.63f, confidence)
            assertEquals(
                "https://via.placeholder.com/512x512?text=20.%20gupfk",
                imageUrl
            )
        }
    }


    @Test
    fun testItemsFetching_when_invalid_json_then_failure()  = runTest {

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("{a = 567}}")

        mockWebServer.enqueue(response)// Act

        try {
            remoteCatalogSource.getItemsMaxId(null)
            fail("This method didn't throw when it was expected to.");
        } catch (ex: java.lang.Exception) {
            //ok
        }
    }

    private fun readFromFile(name:String ) =  File(javaClass.getResource(name)?.path).readText()
}
