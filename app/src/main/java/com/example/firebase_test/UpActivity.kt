package com.example.firebase_test

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.example.firebase_test.databinding.ActivityUpBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class UpActivity : AppCompatActivity() {

    var isEmpty = MutableLiveData(false)
    var isLoading = MutableLiveData(false)

    private val adapter=ImageAdapter()
    private var listImage =MutableLiveData<MutableList<Uri>>()

    private lateinit var binding: ActivityUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isEmpty.value=false
        isLoading.value=true

        binding = ActivityUpBinding.inflate(layoutInflater)
        binding.lifecycleOwner= this
        setContentView(binding.root)

        //khở tạo rycycleview
        initRecyclerView()
        // load anhr vao recycleview de chon anh
        loadImage()

        binding.btnUp.setOnClickListener {
            initFireBase()
        }
        binding.btnContinue.setOnClickListener {
            binding.btnContinue.visibility = View.GONE
            binding.btnUp.visibility = View.VISIBLE
            binding.recycleView.visibility = View.VISIBLE
            binding.btnQuaylai.visibility = View.VISIBLE
            binding.editText.visibility = View.GONE

        }
        binding.btnQuaylai.setOnClickListener {
            binding.btnContinue.visibility = View.VISIBLE
            binding.btnUp.visibility = View.GONE
            binding.recycleView.visibility = View.GONE
            binding.btnQuaylai.visibility = View.GONE
            binding.editText.visibility = View.VISIBLE
        }
    }

    private fun initFireBase() {
        updateImage(adapter.listItemChoices)
    }

    private fun updateImage(listItemChoices: MutableList<Uri>) {
        val listUrl= arrayListOf<String>()
        listItemChoices.forEachIndexed { index, uri ->
            val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
            val storageReference: StorageReference = firebaseStorage.reference
            val imageName = UUID.randomUUID().toString()
            val imageReference = storageReference.child("images").child(imageName)
            imageReference.putFile(uri).addOnSuccessListener {

            // Toast.makeText(this, "Image uploaded", Toast.LENGTH_LONG).show()

            //download url
            val myUploadedImageReference = storageReference.child("images").child(imageName)

            myUploadedImageReference.downloadUrl.addOnSuccessListener { url ->

                val imageURL = url.toString()

                listUrl.add(imageURL)
                if (index == listItemChoices.size-1){
                    addRoomToDatabase(listUrl)
                }
            }

        }.addOnFailureListener {
            if (index == listItemChoices.size-1){
                addRoomToDatabase(listUrl)
            }
            // Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
        }
        }



    }
    private fun addRoomToDatabase(url: ArrayList<String>) {
        val database=FirebaseDatabase.getInstance()
         val myReference: DatabaseReference = database.reference.child("Rooms")


        val id: String = myReference.push().key.toString()

        val room = Room(
            id,
            url,
            binding.editText.text.toString()
        )

        myReference.child(id).setValue(room).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                finish()
            } else {
                Toast.makeText(
                    this,
                    task.exception.toString(),
                    Toast.LENGTH_LONG
                ).show()

            }
        }
    }

    private fun initRecyclerView() {
        listImage.observe(this){
            isLoading.value=false
            if (it.isEmpty()){
                isEmpty.value=true
            }else{

                adapter.list=it
                binding.recycleView.layoutManager=GridLayoutManager(this,3)
                binding.recycleView.adapter=adapter
            }

        }
    }

    private fun loadImage(){
        CoroutineScope (Dispatchers.Default).launch {
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