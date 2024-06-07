package com.marlove.catalog.data.repository.list

import com.marlove.catalog.data.createCatItemWithId
import com.marlove.catalog.data.sameDispatcher
import com.marlove.catalog.data.repository.IRemoteCatalogSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.never
import org.mockito.kotlin.verify


class RefresherTest()
{
    private val allCatalogItems = mutableListOf(
        createCatItemWithId("1"),
        createCatItemWithId("2"),
        createCatItemWithId("3"),
        createCatItemWithId("4") )

    @Test
    fun `when remote storage succeeds`() = runTest {

        val goodRemoteCatalogSource = Mockito.mock(IRemoteCatalogSource::class.java).also {
            Mockito.`when`(it.getItemsSinceID(any())).thenReturn(listOf())
        }

        val refresher = Refresher(
            allCatalogItems,
            goodRemoteCatalogSource,
            sameDispatcher()
        )

        refresher.refresh()

        verify(goodRemoteCatalogSource ).getItemsSinceID("1")
        verify(goodRemoteCatalogSource, never() ).getItemsMaxId(any())
    }


    @Test
    fun `when remote storage fails  expect refresh failing`() =  runTest {

        val badRemoteCatalogSource = Mockito.mock(IRemoteCatalogSource::class.java).also {
            given(it.getItemsSinceID(any())).willAnswer {throw Exception()}
        }

        try {

            val refresher = Refresher(
                allCatalogItems,
                badRemoteCatalogSource,
                sameDispatcher()
            )

            refresher.refresh()
            Assert.fail("Exception is not thrown")
        }catch (_ : Exception){
            //ok
        }
        verify(badRemoteCatalogSource ).getItemsSinceID("1")
        verify(badRemoteCatalogSource, never() ).getItemsMaxId(any())
    }
}