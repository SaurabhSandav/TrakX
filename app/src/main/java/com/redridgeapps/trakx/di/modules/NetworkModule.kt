package com.redridgeapps.trakx.di.modules

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.redridgeapps.trakx.api.TMDbInterceptor
import com.redridgeapps.trakx.api.TMDbService
import dagger.Lazy
import dagger.Module
import dagger.Provides
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.Call.Factory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideTMDbInterceptor(): TMDbInterceptor {
        return TMDbInterceptor()
    }

    @Provides
    @Singleton
    fun provideChuckerInterceptor(app: Application): ChuckerInterceptor {
        return ChuckerInterceptor(app)
    }

    @UnstableDefault
    @Provides
    @Singleton
    fun provideSerializationConverterFactory(): Converter.Factory {
        val contentType = "application/json".toMediaType()
        val configuration = JsonConfiguration.Default.copy(strictMode = false)
        return Json(configuration).asConverterFactory(contentType)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tmDbInterceptor: TMDbInterceptor,
        chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient {
        return OkHttpClient()
            .newBuilder()
            .addInterceptor(tmDbInterceptor)
            .addInterceptor(chuckerInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: Lazy<OkHttpClient>,
        serializationCon: Converter.Factory
    ): Retrofit {

        val callFactory = object : Factory {
            override fun newCall(request: Request) = okHttpClient.get().newCall(request)
        }

        return Retrofit.Builder()
            .baseUrl(TMDbService.BASE_URL)
            .callFactory(callFactory)
            .addConverterFactory(serializationCon)
            .build()
    }

    @Provides
    @Singleton
    fun provideTMDbService(retrofit: Retrofit): TMDbService {
        return retrofit.create(TMDbService::class.java)
    }
}
