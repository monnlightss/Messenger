package com.example.modernmessenger.di

import com.example.modernmessenger.data.repositoryImpl.AuthRepositoryImpl
import com.example.modernmessenger.data.repositoryImpl.ChatRepositoryImpl
import com.example.modernmessenger.data.repositoryImpl.FirebaseStorageRepositoryImpl
import com.example.modernmessenger.data.repositoryImpl.UserRepositoryImpl
import com.example.modernmessenger.domain.repository.AuthRepository
import com.example.modernmessenger.domain.repository.ChatRepository
import com.example.modernmessenger.domain.repository.FirebaseStorageRepository
import com.example.modernmessenger.domain.repository.UserRepository
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Модуль dagger hilt
 * Нужен для объявления функций, которые "строят" наши зависимости
 * **/
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton //означает, что зависимость будет создана только один раз и в дальнейшем будет только передаваться
    fun provideAuthRepository() : AuthRepository{
        return AuthRepositoryImpl()
    }

    @Provides
    @Singleton //означает, что зависимость будет создана только один раз и в дальнейшем будет только передаваться
    fun provideFirebaseStorageRepository() : FirebaseStorageRepository{
        return FirebaseStorageRepositoryImpl()
    }

    @Provides
    @Singleton //означает, что зависимость будет создана только один раз и в дальнейшем будет только передаваться
    fun provideChatRepository(userRepository: Lazy<UserRepository>) : ChatRepository{
        return ChatRepositoryImpl(userRepository)
    }

    @Provides
    @Singleton //означает, что зависимость будет создана только один раз и в дальнейшем будет только передаваться
    fun provideUserRepository(chatRepository: Lazy<ChatRepository>) : UserRepository{
        return UserRepositoryImpl(chatRepository)
    }
}