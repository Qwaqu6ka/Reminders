package com.rome4.feiptest.presentation.async_result

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

typealias FlowResult<T> = StateFlow<AsyncResult<T>>
typealias MutableFlowResult<T> = MutableStateFlow<AsyncResult<T>>

@Immutable
sealed class AsyncResult<T> {
    fun <R> map(mapper: (T) -> R): AsyncResult<R> = when (this) {
        is SuccessResult -> SuccessResult(mapper(this.data))
        is PendingResult -> PendingResult()
        is ErrorResult -> ErrorResult(this.error)
    }

    fun <A, R> combine(other: AsyncResult<A>, transform: (T, A) -> R) =
        if (this is ErrorResult) {
            ErrorResult<R>(this.error)
        } else if (other is ErrorResult) {
            ErrorResult<R>(other.error)
        } else if (this is PendingResult || other is PendingResult) {
            PendingResult<R>()
        } else if (this is SuccessResult && other is SuccessResult) {
            SuccessResult(transform(this.data, other.data))
        } else {
            throw IllegalStateException("Unknown Result state")
        }

    fun takeSuccess(): T? = (this as? SuccessResult)?.data

    @Immutable
    data class SuccessResult<T>(val data: T) : AsyncResult<T>()

    @Immutable
    class PendingResult<T> : AsyncResult<T>()

    @Immutable
    data class ErrorResult<T>(val error: Throwable) : AsyncResult<T>()
}