package com.example.firebase_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.example.firebase_test.databinding.ActivityLoadBinding
import com.example.firebase_test.databinding.ActivityMainBinding

class LoadActivity : AppCompatActivity() {
    var isEmpty= MutableLiveData(false)
    var isLoading= MutableLiveData(false)
    private lateinit var binding: ActivityLoadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)
    }
}