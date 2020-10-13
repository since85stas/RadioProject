package stas.batura.radioproject.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.components.ServiceComponent
import stas.batura.radioproject.data.IRepository
import stas.batura.radioproject.data.Repository

@Module
@InstallIn(ServiceComponent::class)
abstract class ServiceModuleModule {

    @Binds
    abstract fun bindReposService(repositoryS: Repository): IRepository
}