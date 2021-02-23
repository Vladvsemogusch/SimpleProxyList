package cc.anisimov.vlad.simpleproxylist.di.module

import cc.anisimov.vlad.simpleproxylist.data.source.remote.TypicodeApi
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
        const val BASE_URL = "https://jsonplaceholder.typicode.com"
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): TypicodeApi {
        return retrofit.create(TypicodeApi::class.java)
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
}