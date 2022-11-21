package com.example.firebase_test

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.firebase_test.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    companion object{
        private val STORAGE_PERMISSION_UNDER_STORAGE_SCOPE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private val STORAGE_PERMISSION_STORAGE_SCOPE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
    private lateinit var binding:ActivityMainBinding

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (!storagePermissionGrant()) {
                showAlertPermissionNotGrant()
            } else {
                val intent=Intent(this,UpActivity::class.java)
                startActivity(intent)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this

        binding.btnUp.setOnClickListener {
            resultLauncher.launch(getStoragePermissions())
        }
        binding.btnView.setOnClickListener {
            val intent = Intent(this, LoadActivity::class.java)
            startActivity(intent)
        }
    }






    private fun storagePermissionGrant(): Boolean {
        return  allPermissionGrant(this,getStoragePermissions())
    }

    private fun allPermissionGrant(context: Context, intArray: Array<String>): Boolean {
        var isGranted = true
        for (permission in intArray) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                isGranted = false
                break
            }
        }
        return isGranted
    }
    private fun getStoragePermissions(): Array<String> {
        return if (Utils.isAndroidR()) {
            STORAGE_PERMISSION_STORAGE_SCOPE
        } else {
            STORAGE_PERMISSION_UNDER_STORAGE_SCOPE
        }
    }

    private fun hasShowRequestPermissionRationale(
        context: Context?,
        vararg permissions: String?,
    ): Boolean {

            for (permission in permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (context as Activity?)!!,
                        permission!!
                    )
                ) {
                    return true
                }
            }

        return false
    }

    //Xin cấp quyền
    private fun showAlertPermissionNotGrant() {
        if (!hasShowRequestPermissionRationale(this, *getStoragePermissions())) {
            val snackBar = Snackbar.make(
                binding.root,
                "You must approve this permission in Permissions in the app settings on your device to continue using the app.",
                Snackbar.LENGTH_LONG
            )
            snackBar.setAction(
                "Settings"
            ) {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            snackBar.show()
        } else {
            Toast.makeText(
                this,
                "Please grant the application permission to continue using the app",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}