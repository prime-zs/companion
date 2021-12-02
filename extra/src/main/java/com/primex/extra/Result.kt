package com.primex.extra

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.reflect.KProperty
import androidx.compose.runtime.State as AndroidState

/**
 * A class that holds the **UiState**. Since this will only be used UI hence the [AndroidState] observable
 * use [emit] to emit new values.
 *
 *  * Use by to receive the data from within.
 *  * use destructuring to de-structure [Result] into corresponding [State] and [data]
 */
class Result<T>(initial: T) {

    val data: AndroidState<T> = mutableStateOf(initial)

    val state: AndroidState<State> = mutableStateOf(State.Loading)


    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = data.value


    operator fun component2(): T = data.value

    operator fun component1(): State = state.value


    /**
     * Emit new [State] except [State.Success]
     */
    fun emit(new: State) {
        if (new == State.Success)
            throw IllegalAccessException("Use emit(state, value) instead!")
        (state as MutableState).value = new
    }

    /**
     * Emit [State.Success] with new [value].
     */
    fun emit(value: T) {
        (data as MutableState).value = value
        (state as MutableState).value = State.Success
    }


    /**
     * The [State] of the [Result] class.
     */
    sealed class State {
        object Loading : State()
        object Empty : State()
        object Success : State()

        /**
         * @param what: can be anything like string message or some error code as per requirements of user
         */
        data class Error(val what: String) : State()

        /**
         * @param what pass different values for different states like Searching, Processing etc.
         */
        data class Processing(val what: Int = -1) : State()
    }
}
