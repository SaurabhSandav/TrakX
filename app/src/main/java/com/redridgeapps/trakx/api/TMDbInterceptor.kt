package com.redridgeapps.trakx.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class TMDbInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        if (original.url().toString().contains(TMDbService.BASE_URL)) {

            val request = original.newBuilder()
                .url(original.url().withAPIKey())
                .build()

            return chain.proceed(request)
        }

        return chain.proceed(original)
    }

    private fun HttpUrl.withAPIKey(): HttpUrl {
        return newBuilder()
            .addQueryParameter("api_key", TMDbService.TMDB_API_KEY)
            .build()
    }
}
