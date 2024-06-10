package com.rome4.feiptest

import android.app.Application
import com.rome4.feiptest.data.di.dataModule
import com.rome4.feiptest.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                dataModule,
                presentationModule
            )
        }
    }
}