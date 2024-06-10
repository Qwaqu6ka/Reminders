package com.rome4.feiptest.presentation.async_result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rome4.feiptest.R

@Composable
fun <T> ResultRenderer(
    result: AsyncResult<T>,
    onSuccessResult: @Composable BoxScope.(T) -> Unit,
    onPendingResult: @Composable BoxScope.() -> Unit,
    onErrorResult: @Composable BoxScope.(Throwable?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (result) {
            is AsyncResult.SuccessResult -> onSuccessResult(result.data)
            is AsyncResult.PendingResult -> onPendingResult()
            is AsyncResult.ErrorResult -> onErrorResult(result.error)
        }
    }
}

@Composable
fun <T> SimpleResultRenderer(
    result: AsyncResult<T>,
    onTryAgain: () -> Unit,
    modifier: Modifier = Modifier,
    onSuccessResult: @Composable BoxScope.(T) -> Unit
) {
    ResultRenderer(
        result = result,
        onSuccessResult = onSuccessResult,
        onPendingResult = {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        },
        onErrorResult = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(text = stringResource(R.string.result_renderer_error))

                Button(onClick = onTryAgain) {
                    Text(text = stringResource(R.string.try_again))
                }
            }
        },
        modifier = modifier
    )
}