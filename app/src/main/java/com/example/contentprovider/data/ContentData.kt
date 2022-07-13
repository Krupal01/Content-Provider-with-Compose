package com.example.contentprovider.data

import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor

const val CONTENT_ITEM_COLUMN = "contentItem"

class ContentData {

    private var contentList = arrayListOf<String>()

    fun getContentList(): Cursor {
        val matrixCursor = MatrixCursor(arrayOf(CONTENT_ITEM_COLUMN)) // this array for column
        for (i in contentList){
            matrixCursor.addRow(arrayOf(i)) // this array for single row which with all column data
        }
        return matrixCursor
    }

    fun saveContent(content: ContentValues?): Boolean {
        return if (content != null) {
            contentList.add(content.get(CONTENT_ITEM_COLUMN) as String)
        }else{
            false
        }
    }

    fun deleteContent(arg: Array<out String>?): Boolean {
        return if (arg!= null){contentList.removeAll(arg)} else false // we have only one column so no need to use selection
    }

    init {
        for (i in 0..5){
            contentList.add("content $i")
        }
    }
}