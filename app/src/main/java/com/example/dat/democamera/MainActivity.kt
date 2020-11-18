package com.example.dat.democamera

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Dexter.withContext(this@MainActivity)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ,Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object: MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if(report.areAllPermissionsGranted()){

                            }
                        }
                    }
                    override fun onPermissionRationaleShouldBeShown(
                            permissions: MutableList<PermissionRequest>?,
                            token: PermissionToken?
                    ) {
                        // Remember to invoke this method when the custom rationale is closed
                        // or just by default if you don't want to use any custom rationale.
                        token?.continuePermissionRequest()
                    }
                })
                .withErrorListener {

                }
                .check()
        var cameraFragment = CameraFragment()
        if (null == savedInstanceState) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, cameraFragment)
                .commit()
        }
    }


}