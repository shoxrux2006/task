package uz.rezanen.task.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import uz.rezanen.task.di.mainModule

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(mainModule)
            androidContext(this@MyApp)

        }
    }
}