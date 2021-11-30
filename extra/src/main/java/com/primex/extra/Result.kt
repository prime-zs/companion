package com.primex.extra

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.reflect.KProperty
import androidx.compose.runtime.State as AndroidState

/**
 * A class that holds the *UiState*.
 * use [emit] to emit new values. Since This is used
 */
class Result<T>(initial: T) {

    val data: AndroidState<T> = mutableStateOf(initial)

    val state: AndroidState<State> = mutableStateOf(State.Loading)


    operator fun getValue(thisRef: Any?, property: KProperty<*>): State = state.value


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


    sealed class State {
        object Loading : State()
        data class Error(val cause: Throwable?) : State()
        object Success : State()

        /**
         * @param what pass different values for different states like Searching, Processing etc.
         */
        data class Processing(val what: Int = -1) : State()
    }

}