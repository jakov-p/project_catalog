package com.marlove.catalog.utilities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.marlove.catalog.logger.log

//Used for testing
open class EventsLoggerFragment(val fragmentName: String): Fragment()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        printText("on Create")

        savedInstanceState?.let{
             //after configuration change (rotation in our case)
            log.d("***************************************************************************")
            log.d("*                                                                         *")
            log.d("*              R O T A T I O N     H A P P E N E D                        *")
            log.d("*                                                                         *")
            log.d("***************************************************************************")
        }

        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        printText("on View Created")
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onResume() {
        printText("on Resume")
        super.onResume()
    }

    fun printText(methodName:String) {
        log.d("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
        log.d("           ${fragmentName},  $methodName".toUpperCase())
        log.d("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
    }
}