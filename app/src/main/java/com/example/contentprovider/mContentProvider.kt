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
const val uri = "content://$AUTHORITY/$CONTENT_ITEM_COLUMN"
val CONTENT_URI: Uri = Uri.parse(uri)

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

    override fun getType(p0: Uri): String {
        return when (matcher.match(p0)) {
            CONTENT_DATA_CODE -> "vnd.android.cursor.dir/users"
            else -> throw java.lang.IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        val status = contentData.saveContent(p1)
        if (status){
            context?.contentResolver?.notifyChange(CONTENT_URI,null)
            return CONTENT_URI
        }
        throw UnsupportedOperationException("Only reading operations are allowed")
    }

//    USE : contentProvider.delete(CONTENT_URI,"ID=? AND Item=?",new String[]{"256", "item6"})

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        val count: Int
        when(matcher.match(p0)){
            CONTENT_DATA_CODE -> {
                count = if (contentData.deleteContent(p2)) 1 else 0   // we have only one column so no need to use selection
            }
            else ->throw UnsupportedOperationException("Only reading operations are allowed")
        }
        context?.contentResolver?.notifyChange(CONTENT_URI,null)
        return count
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        val count: Int
        when(matcher.match(p0)){
            CONTENT_DATA_CODE -> {
                count = if (contentData.updateContent(p1,p3)) 1 else 0   // we have only one column so no need to use selection
            }
            else ->throw UnsupportedOperationException("Only reading operations are allowed")
        }
        context?.contentResolver?.notifyChange(CONTENT_URI,null)
        return count
    }
}