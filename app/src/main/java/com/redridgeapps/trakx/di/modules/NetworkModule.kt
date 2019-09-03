package com.redridgeapps.trakx.di.modules

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.redridgeapps.trakx.api.TMDbInterceptor
import com.redridgeapps.trakx.api.TMDbService
import dagger.Module
import dagger.Provides
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
object NetworkModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideTMDbInterceptor(): TMDbInterceptor {
        return TMDbInterceptor()
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideChuckerInterceptor(app: Application): ChuckerInterceptor {
        return ChuckerInterceptor(app)
    }

    @UnstableDefault
    @JvmStatic
    @Provides
    @Singleton
    fun provideSerializationConverterFactory(): Converter.Factory {
        val contentType = "application/json".toMediaType()
        val configuration = JsonConfiguration.Default.copy(strictMode = false)
        return Json(configuration).asConverterFactory(contentType)
    }

    @JvmStatic
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

    @JvmStatic
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        serializationCon: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(TMDbService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(serializationCon)
            .build()
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideTMDbService(retrofit: Retrofit): TMDbService {
        return retrofit.create(TMDbService::class.java)
    }
}
