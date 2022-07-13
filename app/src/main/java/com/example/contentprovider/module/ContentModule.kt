package com.example.contentprovider.module

import com.example.contentprovider.data.ContentData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ContentModule {

    @Provides
    @Singleton
    fun getContentData(): ContentData {
        return ContentData()
    }

}