package com.primex.extra

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.primex.extra.Result.State
import kotlinx.coroutines.flow.*
import androidx.compose.runtime.State as AndroidState


/**
 * A utility class that holds the **UiState**.
 *
 * * Since this will only be used UI hence the [AndroidState] observable.
 * * Use [invoke] to emit new values. When emitting data the invoke automatically changes [State] to success.
 * * use destructuring to de-structure [Result] into corresponding [State] and [data]
 */
class Result<T>(initial: T) {
    /* The data of this class*/
    val data: AndroidState<T> = mutableStateOf(initial)
    /*The state of the housed data*/
    val state: AndroidState<State> = mutableStateOf(State.Loading)

    operator fun component2(): T = data.value

    operator fun component1(): State = state.value

    operator fun invoke(value: T) {
        (data as MutableState).value = value
        // automatically change state to success
        invoke(State.Success)
    }

    operator fun invoke(value: State) {
        (state as MutableState).value = value
    }

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