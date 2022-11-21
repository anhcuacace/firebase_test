package com.example.firebase_test

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.firebase_test.databinding.ActivityEditRoomBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList

class EditRoomActivity : AppCompatActivity() {
    lateinit var binding : ActivityEditRoomBinding
     var listAnh = ArrayList<Any>()
     var listImageView = ArrayList<ImageView>()
    var id = " "
    var vitri = -1
    lateinit var activityResultLauncher1: ActivityResultLauncher<Intent>
//    lateinit var activityResultLauncher2: ActivityResultLauncher<Intent>
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listImageView.add(binding.img1)
        listImageView.add(binding.img2)
        listImageView.add(binding.img3)
        listImageView.add(binding.img4)
        listImageView.add(binding.img5)
        listImageView.add(binding.img6)


        var bundle = intent.extras?.getBundle("bundle")
        if (bundle != null) {
            id = bundle.getString("id").toString()
        }
        if (bundle != null) {
           listAnh.addAll(bundle.getStringArrayList("roomAnh")!!)
            listAnh.forEachIndexed { index, any ->
               loadAnh(any,listImageView[index])
           }
            listImageView.forEachIndexed { index, imageView ->
                imageView.setOnClickListener {
                    vitri = index
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
                        )
                    } else {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        intent.action = Intent.ACTION_GET_CONTENT
                        activityResultLauncher1.launch(intent)
                    }
                }
            }

        }
//        listImageView[2].setOnClickListener {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//                != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
//                )
//            } else {
//                val intent = Intent()
//                intent.type = "image/*"
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//                intent.action = Intent.ACTION_GET_CONTENT
//                activityResultLauncher2.launch(intent)
//            }
//        }
        binding.btnDang.setOnClickListener {
           val listCheck = arrayListOf<Int>()
            listAnh.forEachIndexed{ index, imageView ->
                if (imageView.checkUri())
                    listCheck.add(index)
            }

            listAnh.forEachIndexed { index, imageView ->
                if(listCheck.contains(index)){
                    val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
                    val storageReference: StorageReference = firebaseStorage.reference
                    val imageName = UUID.randomUUID().toString()
                    val imageReference = storageReference.child("images").child(imageName)
                    imageReference.putFile(imageView as Uri).addOnSuccessListener {
                        //download url
                        val myUploadedImageReference =
                            storageReference.child("images").child(imageName)

                        myUploadedImageReference.downloadUrl.addOnSuccessListener { url ->

                            listAnh[index] = url.toString()

                            listCheck.remove(index)
                            if (listCheck.isEmpty()){
                                upFireBase()
                            }

                        }
                    }
                }
                else if (index == listAnh.size-1){
                    upFireBase()
                }
            }
        }
        activityResultLauncher()
    }
    fun upFireBase(){
        val roomMap = mutableMapOf<String,Any>()

        roomMap["id"] = id
        roomMap["img"] = listAnh
        roomMap["roomName"] = "Test Edit"


        var myReference: DatabaseReference = database.reference.child("Rooms")
        myReference.child(id).updateChildren(roomMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this,"Hoan Thanh",Toast.LENGTH_SHORT).show()

                var intent = Intent(this,LoadActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    fun loadAnh(img : Any, imgView: ImageView) {
       Glide.with(this).load(img).error(R.drawable.ic_baseline_image_32).into(imgView)
    }
    fun activityResultLauncher(){
        activityResultLauncher1 =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->

                    val resultCode = result.resultCode
                    val imageData = result.data

                    if (resultCode == RESULT_OK && imageData != null) {
                        var  imageUri = imageData.data

                        imageUri?.let {
                           Glide.with(this).load(it).into(listImageView[vitri])
                            if (listAnh.size-1<vitri){
                                vitri = listAnh.size
                                listAnh.add(it)
                            }else{
                                listAnh[vitri] = it
                            }

                        }
                    }
                })
        //
//        activityResultLauncher2 =
//            registerForActivityResult(
//                ActivityResultContracts.StartActivityForResult(),
//                ActivityResultCallback { result ->
//
//                    val resultCode = result.resultCode
//                    val imageData = result.data
//
//                    if (resultCode == RESULT_OK && imageData != null) {
//                        var  imageUri = imageData.data
//
//                        imageUri?.let {
//                            Glide.with(this).load(it).into(listImageView[2])
//                            listAnh[2] = it
//                        }
//                    }
//                })
    }
    fun Any.checkUri():Boolean{
        return this is Uri
    }
}