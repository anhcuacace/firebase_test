package com.example.firebase_test

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.firebase_test.databinding.ActivityUpBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*

class UpActivity : AppCompatActivity() {
    var isEmpty = MutableLiveData(false)
    var isLoading = MutableLiveData(false)
    private var listImage =MutableLiveData<MutableList<Uri>>()
    private lateinit var binding: ActivityUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isEmpty.value=false
        isLoading.value=true
        binding = ActivityUpBinding.inflate(layoutInflater)
        binding.lifecycleOwner= this
        setContentView(binding.root)
        initRecyclerView()
        loadImage()
    }

    private fun initRecyclerView() {
        listImage.observe(this){
            isLoading.value=false
            if (it.isEmpty()){
                isEmpty.value=true
            }else{
                val adapter=ImageAdapter()
                adapter.list=it
                binding.recycleView.adapter=adapter
            }

        }
    }

    private fun loadImage(){
        runBlocking (Dispatchers.Default) {
            listImage.postValue(loadAllImages())
        }
    }


    private fun loadAllImages(): MutableList<Uri> {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        return false.getListFromUri(this, uri )
    }
    private fun Boolean.getListFromUri(
        context: Context,
        uri: Uri
    ): MutableList<Uri> {
        val list = mutableListOf<Uri>()
        val projection =

                arrayOf(
                    MediaStore.MediaColumns._ID,
                    MediaStore.Images.Media.DATA

                )



        createCursor(
            context.contentResolver,
            uri,
            projection,
            this
        ).use { cursor ->
            cursor?.let {
                val idColumn = it.getColumnIndexOrThrow(projection[0])

                val pathColumn =
                    it.getColumnIndexOrThrow(projection[1])


                while (it.moveToNext()) {
                    val path = it.getString(pathColumn)?:"Unknown"
                    val file = File(path)
                    val id = it.getLong(idColumn)


                    if (!file.exists() or file.isHidden or file.isDirectory ) continue
                    val uriImage = ContentUris.withAppendedId(
                        uri,
                        id
                    )
                    list.add(uriImage)
                }
                it.close()
            }

        }
        return list
    }






    private fun createCursor(
        contentResolver: ContentResolver,
        collection: Uri,
        projection: Array<String>,
        orderAscending: Boolean,
    ): Cursor? = when {
        Utils.isAndroidQ() -> {
            val selection =
                createSelectionBundle(orderAscending)
            contentResolver.query(collection, null, selection, null)
        }
        else -> {
            contentResolver.query(collection, projection, null, null, null)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createSelectionBundle(
        orderAscending: Boolean,
    ): Bundle = Bundle().apply {
        putStringArray(
            ContentResolver.QUERY_ARG_SORT_COLUMNS,
            arrayOf(MediaStore.Files.FileColumns.DATE_ADDED)
        )
        val orderDirection =
            if (orderAscending) ContentResolver.QUERY_SORT_DIRECTION_ASCENDING else ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
        putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, orderDirection)
        putString(ContentResolver.QUERY_ARG_SQL_SELECTION, null)
        putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, null)
    }
}