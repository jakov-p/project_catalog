package com.marlove.catalog.domain.repository


/**
 * A wrapper for any kind of action's state.
 */
sealed class ResultState<T> {

    /**
     * Initial or undefined state prior to starting the action
     */
    class Unknown<T> : ResultState<T>() {
        override fun toString(): String {
            return this::class.simpleName!!
        }
    }

    /**
     * The action has started, and has not yet finished.
     * @property data already known data (although the action is still running)
     */
    data class Running<T>(val data: T? = null)  : ResultState<T>()

    /**
     * The action succeeded with a returned data.
     * @property data
     */
    data class Success<T>(val data: T) : ResultState<T>()

    /**
     * The action failed with an returned exception.
     * @property throwable represents a cause of failure
     * @property data usually a partial result (null means not even a partial result)
     */
    data class Error<T>(val throwable: Throwable, val data: T? = null) : ResultState<T>()
}

