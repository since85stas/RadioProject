package stas.batura.radioproject.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.batura.stat.batchat.repository.room.RadioDao
import stas.batura.radioproject.data.room.RadioDatabase
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class RoomModule {

    @Provides
    fun providePressureDao(database: RadioDatabase): RadioDao {
        return database.radioDatabaseDao
    }

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext appContext: Context): RadioDatabase {
        return RadioDatabase.getInstance(appContext)
    }

}