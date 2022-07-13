package com.example.contentprovider.data

import android.database.Cursor
import android.database.MatrixCursor

const val CONTENT_ITEM_COLUMN = "contentItem"

class ContentData {

    fun getContentList(): Cursor {
        val matrixCursor = MatrixCursor(arrayOf(CONTENT_ITEM_COLUMN)) // this array for column
        for (i in 0..5){
            matrixCursor.addRow(arrayOf("ContentItem $i")) // this array for single row which with all column data
        }
        return matrixCursor
    }
}