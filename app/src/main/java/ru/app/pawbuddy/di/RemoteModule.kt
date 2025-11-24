package ru.app.pawbuddy.di

import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.app.pawbuddy.data.remote.RemotePetDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideRemotePetDataSource(db: FirebaseDatabase): RemotePetDataSource =
        RemotePetDataSource(db)
}
