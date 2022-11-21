package com.example.firebase_test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.firebase_test.databinding.ActivityLoadBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList

class LoadActivity : AppCompatActivity() {
    var isEmpty = MutableLiveData(false)
    var isLoading = MutableLiveData(false)
    val adapter = RoomAdapter()
    private var roomList = mutableListOf<Room>()
    private lateinit var binding: ActivityLoadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoadBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        loadRoom()
        adapter.onclickItem={
            if (!it.img.isNullOrEmpty()){
                var bundle = Bundle()
                bundle.putStringArrayList("roomAnh", it.img as ArrayList<String>?)
                bundle.putString("id",it.id)
                val intent =
                    Intent(this, EditRoomActivity::class.java)
                    intent.putExtra("bundle",bundle)
                    startActivity(intent)
                finish()
            }



        }
    }

    private fun loadRoom() {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val myReference: DatabaseReference = database.reference.child("Rooms")
        myReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (data in it.result.children) {
                    val id = data.key.toString()
                    val name = data.child("roomName").value.toString()
                    val list = data.child("img").value as List<String>
                    roomList.add(Room(id, list, name))
                }

                adapter.roomList = roomList
                binding.recycleView.adapter = adapter

            } else {
                Log.e("sdsds", it.exception?.message.toString())
            }
        }
    }
}