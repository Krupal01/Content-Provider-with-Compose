package com.example.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.contentprovider.data.CONTENT_ITEM_COLUMN
import com.example.contentprovider.data.ContentData
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

const val CONTENT_TABLE = CONTENT_ITEM_COLUMN
const val AUTHORITY = "com.example.contentprovider.provider"
const val CONTENT_DATA_CODE = 1

class mContentProvider : ContentProvider() {
    private lateinit var contentData : ContentData
    private lateinit var appContext: Context
    override fun onCreate(): Boolean {
        appContext = context?.applicationContext ?: throw IllegalStateException()
        contentData = getContentData(appContext)
        return true
    }

    private val matcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, CONTENT_TABLE, CONTENT_DATA_CODE)
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface ContentDataProviderEntryPoint{
        fun getData() : ContentData
    }

    private fun getContentData(appContext : Context):ContentData{
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            appContext,
            ContentDataProviderEntryPoint::class.java
        )
        return hiltEntryPoint.getData()
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? {
        val resultCode : Int = matcher.match(p0)
        return if (resultCode == CONTENT_DATA_CODE){

            val cursor : Cursor? =  if (resultCode == CONTENT_DATA_CODE){
                contentData.getContentList()
            }else{
                null
            }
            cursor?.setNotificationUri(appContext.contentResolver,p0)
            cursor
        }else{
            throw IllegalArgumentException("Unknown URI: $p0")
        }
    }

    override fun getType(p0: Uri): String? {
        throw UnsupportedOperationException("Only reading operations are allowed")
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        throw UnsupportedOperationException("Only reading operations are allowed")
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        throw UnsupportedOperationException("Only reading operations are allowed")
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        throw UnsupportedOperationException("Only reading operations are allowed")
    }
}