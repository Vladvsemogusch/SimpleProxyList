package cc.anisimov.vlad.simpleproxylist.di.module

import cc.anisimov.vlad.simpleproxylist.data.source.remote.ProxyApi
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {
    companion object{
        const val BASE_URL = "https://wnnzn.sse.codesandbox.io"
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): ProxyApi {
        return retrofit.create(ProxyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
}