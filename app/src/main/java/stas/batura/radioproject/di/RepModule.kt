package stas.batura.radioproject.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import stas.batura.radioproject.data.IRepository
import stas.batura.radioproject.data.Repository
import javax.inject.Singleton

//@Module
//@InstallIn(ActivityComponent::class)
//abstract class IRepositoryModule {
//
//    @Binds
//    abstract fun bindRepos(repository: Repository): IRepository
//}

//@Module
//@Singleton
//class RepModule {
//
//    fun providesRepository(): Repository {
//        return Repository()
//    }
//
//}

@Module
@InstallIn(ActivityComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindRepos(repository: Repository): IRepository
}