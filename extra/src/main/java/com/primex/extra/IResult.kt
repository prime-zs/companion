package com.primex.extra

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlin.reflect.KProperty
import androidx.compose.runtime.State as AndroidState

/**
 * A class that holds the **UiState**.
 *
 *
 *  * Use by to receive the state from within.
 *  * use destructuring to de-structure [IResult] into corresponding [State] and [data]
 *  * Note: use [emit] to emit new states.
 */
abstract class IResult<T>(initial: State = State.Loading) {

    abstract val data: StateFlow<T>

    val state: AndroidState<State> = mutableStateOf(initial)

    protected infix fun AndroidState<State>.emit(new: State) {
        (this as MutableState).value = new
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): State = state.value

    operator fun component2(): StateFlow<T> = data

    operator fun component1(): State = state.value

    /**
     * The [State] of the [IResult] class.
     */
    sealed class State {
        object Loading : State()
        /**
         * @param what pass different values for different states like Searching, Processing etc.
         */
        data class Processing(val what: Int = -1) : State()
        /**
         * @param what: can be anything like string message or some error code as per requirements of user
         */
        data class Error(val what: String) : State()

        object Empty : State()

        object Success : State()
    }
}