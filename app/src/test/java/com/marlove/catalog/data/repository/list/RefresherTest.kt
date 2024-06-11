package com.marlove.catalog.data.repository.list

import com.marlove.catalog.data.createCatItemWithId
import com.marlove.catalog.data.repository.ILocalCatalogSource
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


class RefresherTest() {
    private val allCatalogItems = mutableListOf(
        createCatItemWithId("10"),
        createCatItemWithId("11"),
        createCatItemWithId("12"),
        createCatItemWithId("13")
    )



    @Test
    fun `when remote storage succeeds`() = runTest {

        val returnedFromRemote = listOf(
            createCatItemWithId("1"),
            createCatItemWithId("2"))

        val localCatalogSource = Mockito.mock(ILocalCatalogSource::class.java)

        val goodRemoteCatalogSource = Mockito.mock(IRemoteCatalogSource::class.java).also {
            Mockito.`when`(it.getItemsSinceID(any())).thenReturn(returnedFromRemote)
        }

        val refresher = Refresher(
            allCatalogItems,
            localCatalogSource,
            goodRemoteCatalogSource,
            sameDispatcher()
        )

        refresher.refresh()

        Assert.assertEquals(2, allCatalogItems.size)

        verify(goodRemoteCatalogSource).getItemsSinceID("10")
        verify(goodRemoteCatalogSource, never()).getItemsMaxId(any())

        verify(localCatalogSource).deleteAll()
        verify(localCatalogSource).insertWithoutImage(returnedFromRemote)
    }


    @Test
    fun `when remote storage fails  expect refresh failing`() = runTest {

        val localCatalogSource = Mockito.mock(ILocalCatalogSource::class.java)

        val badRemoteCatalogSource = Mockito.mock(IRemoteCatalogSource::class.java).also {
            given(it.getItemsSinceID(any())).willAnswer { throw Exception() }
        }

        try {

            val refresher = Refresher(
                allCatalogItems,
                localCatalogSource,
                badRemoteCatalogSource,
                sameDispatcher()
            )

            refresher.refresh()
            Assert.fail("Exception is not thrown")
        } catch (_: Exception) {
            //ok
        }
        verify(badRemoteCatalogSource).getItemsSinceID("10")
        verify(badRemoteCatalogSource, never()).getItemsMaxId(any())
    }


}