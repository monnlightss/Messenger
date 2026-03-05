package com.example.modernmessenger.di

import com.example.modernmessenger.data.usecase.CreateNewChatUseCase
import com.example.modernmessenger.domain.repository.ChatRepository
import com.example.modernmessenger.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Аналогичный модуль, как RepositoryModule.kt, но для того, чтобы собирать usecase **/
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideCreateNewChatUseCase(chatRepository: ChatRepository, userRepository: UserRepository) : CreateNewChatUseCase{
        return CreateNewChatUseCase(chatRepository, userRepository)
    }
}