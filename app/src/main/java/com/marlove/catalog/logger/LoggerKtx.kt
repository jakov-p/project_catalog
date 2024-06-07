package com.marlove.catalog.logger

import co.touchlab.kermit.Logger
import co.touchlab.kermit.mutableLoggerConfigInit
import co.touchlab.kermit.platformLogWriter

val logger = Logger(mutableLoggerConfigInit(listOf(platformLogWriter())))

inline val <reified T : Any> T.log: Logger get() = log()

inline fun <reified T : Any> T.log(tag: String? = this::class.simpleName) =
    when (tag) {
        null -> logger
        else -> logger.withTag(tag)
    }



inline fun <reified T : Any>  T.printTitle(message:String) {

    log.d(" ")
    log.d(  "***************************************************************************************************")
    log.d(  "*                                                                                                 *")
    log.d(  "*                             " + message + "")
    log.d(  "*                                                                                                 *")
    log.d(  "***************************************************************************************************")
    log.d("")
}

inline fun <reified T : Any>  T.printSubtitle(message: String) {

    log.i("****************************************************************************** ")
    log.i("                       $message ...")
    log.i("****************************************************************************** ")
}