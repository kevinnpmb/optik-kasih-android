package com.skripsi.optik_kasih.di

import android.content.Context
import androidx.room.Room
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.google.gson.Gson
import com.skripsi.optik_kasih.BuildConfig
import com.skripsi.optik_kasih.api.NetworkResource
import com.skripsi.optik_kasih.api.RequestInterceptor
import com.skripsi.optik_kasih.database.OptikKasihDatabase
import com.skripsi.optik_kasih.vo.RequestHeaders
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun provideOptikKasihDatabase(@ApplicationContext appContext: Context) = Room.databaseBuilder(
        appContext,
        OptikKasihDatabase::class.java,
        "optik_kasih.db"
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideCartDao(database: OptikKasihDatabase) = database.cartDao()

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    @Provides
    @Singleton
    fun provideRequestHeader(): RequestHeaders {
        return RequestHeaders(language = "application/json")
    }

    @Provides
    @Singleton
    fun provideRequestInterceptor(requestHeaders: RequestHeaders): RequestInterceptor {
        return RequestInterceptor(requestHeaders)
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(
        logging: HttpLoggingInterceptor,
        requestInterceptor: RequestInterceptor
    ): OkHttpClient {
        var cipherSuites: MutableList<CipherSuite>? =
            ConnectionSpec.MODERN_TLS.cipherSuites as MutableList<CipherSuite>?
        if (!cipherSuites!!.contains(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)) {
            cipherSuites = ArrayList(cipherSuites)
            cipherSuites.add(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)
        }
        val spec = listOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS)
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectionSpecs(spec)
            .addInterceptor(logging)
            .retryOnConnectionFailure(true)
            .addInterceptor(requestInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideApolloClient(client: OkHttpClient): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(BuildConfig.BASE_URL)
            .okHttpClient(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideNetworkResource(@ApplicationContext appContext: Context): NetworkResource {
        return NetworkResource(appContext)
    }
}
