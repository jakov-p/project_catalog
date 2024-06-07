package com.marlove.catalog.data

import com.marlove.catalog.domain.model.CatalogItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

fun createCatItemWithId(id:String) =
    CatalogItem(id, "text_$id", 0.05f, "https://aaaaa_$id.jpg")

fun sameDispatcher():CoroutineDispatcher = UnconfinedTestDispatcher()