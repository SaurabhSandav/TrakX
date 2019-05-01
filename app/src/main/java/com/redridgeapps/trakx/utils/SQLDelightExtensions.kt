@file:JvmName("FlowQuery")

package com.redridgeapps.trakx.utils

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowViaChannel
import kotlinx.coroutines.flow.flowWith
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/** Turns this [Query] into a [Flow] which emits whenever the underlying result set changes. */
@FlowPreview
@JvmName("toFlow")
fun <T : Any> Query<T>.asFlow(): Flow<Query<T>> = flowViaChannel(CONFLATED) { channel ->
    val listener = object : Query.Listener {
        override fun queryResultsChanged() {
            channel.offer(this@asFlow)
        }
    }
    addListener(listener)
    @Suppress("EXPERIMENTAL_API_USAGE") //TODO: Remove when invokeOnClose is no longer experimental, or use replacement.
    channel.invokeOnClose {
        removeListener(listener)
    }
    channel.offer(this@asFlow)
}

@FlowPreview
fun <T : Any> Flow<Query<T>>.mapToOne(
    context: CoroutineContext = Dispatchers.IO
): Flow<T> {
    return flowWith(context) {
        map { it.executeAsOne() }
    }
}

@FlowPreview
fun <T : Any> Flow<Query<T>>.mapToOneOrDefault(
    defaultValue: T,
    context: CoroutineContext = Dispatchers.IO
): Flow<T> {
    return flowWith(context) {
        map { it.executeAsOneOrNull() ?: defaultValue }
    }
}

@FlowPreview
fun <T : Any> Flow<Query<T>>.mapToOneOrNull(
    context: CoroutineContext = Dispatchers.IO
): Flow<T?> {
    return flowWith(context) {
        map { it.executeAsOneOrNull() }
    }
}

@FlowPreview
fun <T : Any> Flow<Query<T>>.mapToOneNonNull(
    context: CoroutineContext = Dispatchers.IO
): Flow<T> {
    return flowWith(context) {
        mapNotNull { it.executeAsOneOrNull() }
    }
}

@FlowPreview
fun <T : Any> Flow<Query<T>>.mapToList(
    context: CoroutineContext = Dispatchers.IO
): Flow<List<T>> {
    return flowWith(context) {
        map { it.executeAsList() }
    }
}

suspend fun <T : Any> Query<T>.suspendAsOne(
    context: CoroutineContext = Dispatchers.IO
): T {
    return withContext(context) { executeAsOne() }
}

suspend fun <T : Any> Query<T>.suspendAsOneOrDefault(
    defaultValue: T,
    context: CoroutineContext = Dispatchers.IO
): T {
    return withContext(context) { executeAsOneOrNull() ?: defaultValue }
}

suspend fun <T : Any> Query<T>.suspendAsOneOrNull(
    context: CoroutineContext = Dispatchers.IO
): T? {
    return withContext(context) { executeAsOneOrNull() }
}

suspend fun <T : Any> Query<T>.suspendAsList(
    context: CoroutineContext = Dispatchers.IO
): List<T> {
    return withContext(context) { executeAsList() }
}

suspend fun Transacter.suspendedTransaction(
    context: CoroutineContext = Dispatchers.IO,
    noEnclosing: Boolean? = null,
    body: Transacter.Transaction.() -> Unit
) = withContext(context) {
    if (noEnclosing == null)
        transaction(body = body)
    else
        transaction(noEnclosing, body)
}
