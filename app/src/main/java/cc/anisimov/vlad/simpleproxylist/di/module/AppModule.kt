package cc.anisimov.vlad.simpleproxylist.di.module

import cc.anisimov.vlad.simpleproxylist.data.source.remote.ProxyApi
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
class AppModule {
    companion object {
        const val BASE_URL = "https://wnnzn.sse.codesandbox.io"
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): ProxyApi {
        return retrofit.create(ProxyApi::class.java)

    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .addInterceptor {
                val request: Request = it.request()
                val t = it.proceed(request)
                return@addInterceptor t
            }
            .build()
    }


    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
}