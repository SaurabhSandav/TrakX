package com.redridgeapps.trakx.di.modules

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.preference.PreferenceManager
import com.ashokvarma.gander.GanderInterceptor
import com.redridgeapps.trakx.AppDatabase
import com.redridgeapps.trakx.InMemoryCacheDatabase
import com.redridgeapps.trakx.api.TMDbInterceptor
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.db.SqLiteDriverCallback
import com.redridgeapps.trakx.utils.moshi.LongDateAdapter
import com.squareup.moshi.Moshi
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
object AppModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideResources(app: Application): Resources {
        return app.resources
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideSharedPreferences(app: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideTMDbInterceptor(): TMDbInterceptor {
        return TMDbInterceptor()
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideGanderInterceptor(app: Application): GanderInterceptor {
        return GanderInterceptor(app)
            .showNotification(true)
            .retainDataFor(GanderInterceptor.Period.FOREVER)
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(LongDateAdapter())
            .build()
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory {
        return MoshiConverterFactory.create(moshi)
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideOkHttpClient(
        tmDbInterceptor: TMDbInterceptor,
        ganderInterceptor: GanderInterceptor
    ): OkHttpClient {
        return OkHttpClient()
            .newBuilder()
            .addInterceptor(tmDbInterceptor)
            .addInterceptor(ganderInterceptor)
            .build()
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshiCon: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(TMDbService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(moshiCon)
            .build()
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideTMDbService(retrofit: Retrofit): TMDbService {
        return retrofit.create(TMDbService::class.java)
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideInMemoryCacheDatabase(app: Application): InMemoryCacheDatabase {

        val schema = InMemoryCacheDatabase.Schema
        val callback = SqLiteDriverCallback(schema)
        val driver = AndroidSqliteDriver(schema, app, callback = callback)

        return InMemoryCacheDatabase(driver)
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {

        val schema = AppDatabase.Schema
        val callback = SqLiteDriverCallback(schema)
        val driver = AndroidSqliteDriver(schema, app, "AppDatabase.db", callback = callback)

        return AppDatabase(driver)
    }
}
