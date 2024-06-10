package com.rome4.feiptest.data.di

import androidx.room.Room
import com.rome4.feiptest.data.repositoies.RemindersRepository
import com.rome4.feiptest.data.repositoies.RemindersRepositoryImpl
import com.rome4.feiptest.data.sources.ClientsSource
import com.rome4.feiptest.data.sources.room.AppDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val KOIN_IO_DISPATCHER = "io_dispatcher"

val dataModule = module {

    single<CoroutineDispatcher>(named(KOIN_IO_DISPATCHER)) { Dispatchers.IO }

    single<AppDatabase> {
        Room.databaseBuilder(
            context = get(),
            klass = AppDatabase::class.java,
            name = "feip_reminders"
        ).build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://randomuser.me/api/")
            .addConverterFactory(
                Json {
                    ignoreUnknownKeys = true
                }.asConverterFactory("application/json; charset=UTF8".toMediaType())
            )
            .build()
    }

    single<ClientsSource> {
        val retrofit: Retrofit = get()
        retrofit.create(ClientsSource::class.java)
    }

    single<RemindersRepository> {
        RemindersRepositoryImpl(
            ioDispatcher = get(named(KOIN_IO_DISPATCHER)),
            clientsSource = get(),
            room = get(),
        )
    }
}