package com.example.contentprovider

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contentprovider.data.CONTENT_ITEM_COLUMN
import com.example.contentprovider.ui.theme.ContentProviderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val contentItems = remember {
                mutableStateListOf<String>()
            }

            Column(modifier = Modifier.fillMaxSize()) {

                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    val cursor: Cursor? = contentResolver.query(
                        Uri.parse("content://$AUTHORITY/$CONTENT_ITEM_COLUMN"),
                        null,
                        null,
                        null,
                        null
                    )

                    cursor?.let {
                        contentItems.clear()
                        if(cursor.moveToFirst()) {
                            while (!cursor.isAfterLast()) {
                                contentItems.add(cursor.getString(cursor.getColumnIndex(CONTENT_ITEM_COLUMN)))
                                cursor.moveToNext()
                            }
                        }
                        else {
                            contentItems.add("no record found")
                        }
                    }
                }) {
                    Text(text = "Load Content ")
                }

                Button(modifier = Modifier.fillMaxWidth(), onClick = {

                    val contentValue = ContentValues().also {
                        it.put(CONTENT_ITEM_COLUMN,"content new")
                    }
                    contentResolver.insert(CONTENT_URI,contentValue)

                }) {
                    Text(text = "Add Content ")
                }

                ContentProviderTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        LazyColumn{
                            items(contentItems){item : String ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(all = 10.dp)
                                ) {
                                    Text(
                                        text = item ,
                                        modifier = Modifier
                                            .weight(1f)
                                    )
                                    IconButton(onClick = {
                                        val code = contentResolver.delete(CONTENT_URI, CONTENT_ITEM_COLUMN, arrayOf(item))
                                        Toast.makeText(applicationContext,if(code==1)"Delete Success,Load Again" else "Delete Fail , Retry",Toast.LENGTH_LONG).show()
                                    }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription ="Delete" )
                                    }

                                    IconButton(onClick = {
                                        val contentValue = ContentValues().also {
                                            it.put(CONTENT_ITEM_COLUMN,"content updated")
                                        }
                                        val code = contentResolver.update(CONTENT_URI, contentValue,CONTENT_ITEM_COLUMN, arrayOf(item)).let {
                                            Toast.makeText(applicationContext,if(it==1)"Update Success,Load Again" else "Update Fail , Retry",Toast.LENGTH_LONG).show()
                                        }
                                    }) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription ="Edit" )
                                    }

                                }

                            }
                        }
                    }
                }
            }

        }
    }
}
