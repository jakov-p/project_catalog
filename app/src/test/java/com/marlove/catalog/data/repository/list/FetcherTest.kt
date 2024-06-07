package com.marlove.catalog.data.repository.list

import com.marlove.catalog.data.createCatItemWithId
import com.marlove.catalog.data.sameDispatcher
import com.marlove.catalog.data.repository.ILocalCatalogSource
import com.marlove.catalog.data.repository.IRemoteCatalogSource
import com.marlove.catalog.domain.model.CatalogItem
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

//<>

@RunWith(MockitoJUnitRunner::class)
class FetcherTest {

    private val allCatalogItems = mutableListOf(
        createCatItemWithId("1"),
        createCatItemWithId("2"),
        createCatItemWithId("3"),
        createCatItemWithId("4") )

    val localCatalogSource = Mockito.mock(ILocalCatalogSource::class.java)


    @Test
    fun `when all items are local expect not fetching remotely`() = runTest {

        val goodRemoteCatalogSource = Mockito.mock(IRemoteCatalogSource::class.java)

        val fetcher = Fetcher(
            allCatalogItems,
            localCatalogSource,
            goodRemoteCatalogSource,
            sameDispatcher()
        )

        val itemList = fetcher.getCatalogItems(0, 2)
        assertEquals(2, itemList.size)

        verify(localCatalogSource, never()).insertWithoutImage(any())
        verify(goodRemoteCatalogSource, never()).getItemsSinceID(any())
    }


    @Test
    fun `when all items are not local expect fetching remotely`()  = runTest{

        val fetchedItemsFromRemote = listOf(
            createCatItemWithId("5"),
            createCatItemWithId("7"),
            createCatItemWithId("8"),
            createCatItemWithId("9"),
            createCatItemWithId("10"),
            createCatItemWithId("11"))

        val goodRemoteCatalogSource = Mockito.mock(IRemoteCatalogSource::class.java).also {
            Mockito.`when`(it.getItemsMaxId(anyString())).thenReturn(fetchedItemsFromRemote)
        }

        val fetcher = Fetcher(
            allCatalogItems,
            localCatalogSource,
            goodRemoteCatalogSource,
            sameDispatcher()
        )

        val previousSize = allCatalogItems.size

        val itemList = fetcher.getCatalogItems(0, 5)
        assertEquals(5, itemList.size)

        assertEquals(fetchedItemsFromRemote.size + previousSize,
                    allCatalogItems.size)

        verify(localCatalogSource).insertWithoutImage(fetchedItemsFromRemote)
        verify(goodRemoteCatalogSource).getItemsMaxId("4")
    }


    @Test
    fun `when too many are requested  expect fetching remotely until empty returned`() = runTest {

        val fetchedRemote_4 = listOf(
            createCatItemWithId("5"),
            createCatItemWithId("6"),
            createCatItemWithId("7")
        )

        val fetchedRemote_7 = listOf(
            createCatItemWithId("8"),
            createCatItemWithId("9"),
            createCatItemWithId("10"))

        val fetchedRemote_10 = listOf<CatalogItem>()

        val goodRemoteCatalogSource = Mockito.mock(IRemoteCatalogSource::class.java).also {
            Mockito.`when`(it.getItemsMaxId("4")).thenReturn(fetchedRemote_4)
            Mockito.`when`(it.getItemsMaxId("7")).thenReturn(fetchedRemote_7)
            Mockito.`when`(it.getItemsMaxId("10")).thenReturn(fetchedRemote_10)
        }

        val fetcher = Fetcher(
            allCatalogItems,
            localCatalogSource,
            goodRemoteCatalogSource,
            sameDispatcher()
        )

        val previousSize = allCatalogItems.size

        val itemList = fetcher.getCatalogItems(0, 14)
        assertEquals(10,  itemList.size)

        assertEquals( previousSize+ fetchedRemote_4.size + fetchedRemote_7.size,
            allCatalogItems.size)

        verify(localCatalogSource, times(2)).insertWithoutImage(any())
        verify(localCatalogSource).insertWithoutImage(fetchedRemote_4)
        verify(localCatalogSource).insertWithoutImage(fetchedRemote_7)


        verify(goodRemoteCatalogSource, times(3)).getItemsMaxId(any())
        verify(goodRemoteCatalogSource).getItemsMaxId("4")
        verify(goodRemoteCatalogSource).getItemsMaxId("7")
        verify(goodRemoteCatalogSource).getItemsMaxId("10")
    }

    @Test
    fun `when fetching remotely fails expect exception`() =  runTest {

        val badRemoteCatalogSource = Mockito.mock(IRemoteCatalogSource::class.java).also {
            given(it.getItemsMaxId(anyString())).willAnswer{ throw Exception()}
        }

        val fetcher = Fetcher(
            allCatalogItems,
            localCatalogSource,
            badRemoteCatalogSource,
            sameDispatcher()
        )

        try {
            fetcher.getCatalogItems(0, 11)
            fail("Exception is not thrown")
        }catch (_ : Exception){
            //ok
        }

        verify(localCatalogSource, never()).insertWithoutImage(any())
        verify(badRemoteCatalogSource).getItemsMaxId(any())
    }


}
