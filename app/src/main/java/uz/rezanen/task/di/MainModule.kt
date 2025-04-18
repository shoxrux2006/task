package uz.rezanen.task.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import uz.rezanen.task.domain.CardRepository
import uz.rezanen.task.domain.LoginRepository
import uz.rezanen.task.domain.WalletRepository
import uz.rezanen.task.domain.impl.CardRepositoryImpl
import uz.rezanen.task.domain.impl.LoginRepositoryImpl
import uz.rezanen.task.domain.impl.WalletRepositoryImpl
import uz.rezanen.task.navigation.AppNavigation
import uz.rezanen.task.navigation.NavigationDispatcher
import uz.rezanen.task.navigation.NavigationHandler
import uz.rezanen.task.presentation.addCard.vm.AddCardVM
import uz.rezanen.task.presentation.addCard.vm.AddCardVMImpl
import uz.rezanen.task.presentation.login.vm.LoginVM
import uz.rezanen.task.presentation.login.vm.LoginVMImpl
import uz.rezanen.task.presentation.wallet.vm.WalletVM
import uz.rezanen.task.presentation.wallet.vm.WalletVMImpl
import uz.rezanen.task.remote.providers.KtorClientProvider
import uz.rezanen.task.remote.providers.PhoneNumberProvider
import uz.rezanen.task.remote.service.MainApiService
import uz.rezanen.task.shp.Shp

val mainModule = module {
    single { NavigationDispatcher() }
    single<AppNavigation> { get<NavigationDispatcher>() }
    single<NavigationHandler> { get<NavigationDispatcher>() }

    single { PhoneNumberProvider(get()) }

    single { KtorClientProvider(get()) }
    single { get<KtorClientProvider>().provide() }
    single { MainApiService(get()) }

    single { Shp(get()) }


    single<CardRepository> { CardRepositoryImpl(get(), get()) }
    single<WalletRepository> { WalletRepositoryImpl(get(), get()) }
    single<LoginRepository> { LoginRepositoryImpl(get(), get(), get()) }


    viewModel { AddCardVMImpl(get(), get()) }
    factory<AddCardVM> { get<AddCardVMImpl>() }

    viewModel { LoginVMImpl(get(), get()) }
    factory<LoginVM> { get<LoginVMImpl>() }

    viewModel { WalletVMImpl(get(), get(), get(), get()) }
    factory<WalletVM> { get<WalletVMImpl>() }
}