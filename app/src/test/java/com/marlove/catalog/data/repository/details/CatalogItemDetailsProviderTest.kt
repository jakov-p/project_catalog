package com.marlove.catalog.data.repository.details

import com.marlove.catalog.data.createCatItemWithId
import com.marlove.catalog.data.sameDispatcher
import com.marlove.catalog.data.repository.ILocalCatalogSource
import com.marlove.catalog.data.source.remote.retrofit.utilities.IFileDownloader
import com.marlove.catalog.domain.model.CatalogItemDetails
import com.marlove.catalog.domain.repository.ResultState
import com.marlove.catalog.domain.repository.ResultState.*
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

//<>

@RunWith(MockitoJUnitRunner::class)
class CatalogItemDetailsProviderTest {

    private val ID_FOR_ITEM_WITH_IMAGE = "1"
    private val ID_FOR_ITEM_NO_IMAGE = "2"

    private val allCatalogItems = mutableListOf(
        createCatItemWithId(ID_FOR_ITEM_WITH_IMAGE),
        createCatItemWithId(ID_FOR_ITEM_NO_IMAGE))

    private val goodImageBytes: ByteArray = byteArrayOf(0x0E.toByte(), 0x0F.toByte())
    private val localCatalogSource = Mockito.mock(ILocalCatalogSource::class.java).also {
        Mockito.`when`(it.getImage(ID_FOR_ITEM_WITH_IMAGE)).thenReturn(goodImageBytes)
        Mockito.`when`(it.getImage(ID_FOR_ITEM_NO_IMAGE)).thenReturn(null)
    }

    private val goodFileDownloader = Mockito.mock(IFileDownloader::class.java).also {
        Mockito.`when`(it.download()).thenReturn(goodImageBytes)
    }

    private val badFileDownloader = Mockito.mock(IFileDownloader::class.java).also {
        given(it.download()).willAnswer{throw Exception("Failed")}
    }

    @Test
    fun `when image in dbase expect fetching from dbase`()  = runTest{

        val catalogItemDetailsProvider = CatalogItemDetailsProvider(
            localCatalogSource,
            allCatalogItems,
            { goodFileDownloader },
            sameDispatcher(),
            ID_FOR_ITEM_WITH_IMAGE
        )


        val emittedValues = mutableListOf<ResultState<CatalogItemDetails>>()
        catalogItemDetailsProvider.getFlow().toCollection(emittedValues)

        val partialDetails = CatalogItemDetails(createCatItemWithId(ID_FOR_ITEM_WITH_IMAGE), null)
        assertEquals(Running(partialDetails), emittedValues[0])

        val fullDetails = CatalogItemDetails(createCatItemWithId(ID_FOR_ITEM_WITH_IMAGE), goodImageBytes)
        assertEquals(Success(fullDetails), emittedValues[1])

        verify(localCatalogSource).getImage(ID_FOR_ITEM_WITH_IMAGE)
        verify(goodFileDownloader, never()).download()
    }

    @Test
    fun `when image not in dbase expect fetching from remote with SUCCESS`() = runTest {

        val catalogItemDetailsProvider = CatalogItemDetailsProvider(
            localCatalogSource,
            allCatalogItems,
            { goodFileDownloader },
            sameDispatcher(),
            ID_FOR_ITEM_NO_IMAGE
        )


        val emittedValues = mutableListOf<ResultState<CatalogItemDetails>>()
        catalogItemDetailsProvider.getFlow().toCollection(emittedValues)

        val partialDetails = CatalogItemDetails(createCatItemWithId(ID_FOR_ITEM_NO_IMAGE), null)
        assertEquals(Running(partialDetails), emittedValues[0])

        val fullDetails = CatalogItemDetails(createCatItemWithId(ID_FOR_ITEM_NO_IMAGE), goodImageBytes)
        assertEquals(Success(fullDetails), emittedValues[1])

        verify(goodFileDownloader ).download()
        verify(localCatalogSource).updateImage(ID_FOR_ITEM_NO_IMAGE, goodImageBytes)
    }

    @Test
    fun `when image not in dbase expect fetching from remote with FAILURE`() = runTest {

        val catalogItemDetailsProvider = CatalogItemDetailsProvider(
            localCatalogSource,
            allCatalogItems,
            { badFileDownloader },
            sameDispatcher(),
            ID_FOR_ITEM_NO_IMAGE
        )


        val emittedValues = mutableListOf<ResultState<CatalogItemDetails>>()
        catalogItemDetailsProvider.getFlow().toCollection(emittedValues)

        val partialDetails = CatalogItemDetails(createCatItemWithId(ID_FOR_ITEM_NO_IMAGE), null)
        assertEquals(Running(partialDetails), emittedValues[0])

        assertTrue( emittedValues[1] is Error)
        assertEquals(partialDetails,(emittedValues[1] as Error).data )

        verify(badFileDownloader).download()
        verify(localCatalogSource, never()).updateImage(any(), any())
    }

}
