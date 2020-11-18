package com.example.dat.democamera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import java.util.*

object ImageUtils {
    var path = ""
    fun getAllImage(context: Context): List<AlbumImage?>? {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        val mLocalImages: MutableList<LocalImage> =
            ArrayList()
        val columns =
            arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
        val orderBy = MediaStore.Images.Media._ID + " DESC"
        val cc = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
            null, orderBy
        )
        return if (cc != null) {
            cc.moveToFirst()
            for (i in 0 until cc.count) {
                cc.moveToPosition(i)
                val id = cc.getInt(cc.getColumnIndex(MediaStore.MediaColumns._ID))
                val uri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "" + id
                )
                val name = cc.getString(1)
                val path = cc.getString(0)
                val localImage = LocalImage()
                localImage.uri = uri
                localImage.path =path
                localImage.name =name
                mLocalImages.add(localImage)
            }
            cc.close()
            getAlbumImage(mLocalImages)

        } else {
            null
        }
    }

    private fun getAlbumImage(localImages: List<LocalImage>): List<AlbumImage?> {
        val albumImages: MutableList<AlbumImage?> =
            ArrayList()
        for (localImage in localImages) {
            if (!checkAlbum(albumImages, localImage)) {
                val localImagesAlbum: MutableList<LocalImage?> =
                    ArrayList()
                localImagesAlbum.add(localImage)
                val albumImage =
                    AlbumImage(localImagesAlbum, getAlbumName(localImage.path))
                albumImage.path = getPathAlbum(localImage.path)
                albumImages.add(albumImage)
            }
        }
        return albumImages
    }

    private fun getAlbumName(path: String?): String {
        if (path == null) return ""
        val names = path.split("/".toRegex()).toTypedArray()
        var name = ""
        try {
            name = names[names.size - 2]
        } catch (e: ArrayIndexOutOfBoundsException) {
            e.printStackTrace()
        }
        return name
    }

    private fun getPathAlbum(path: String?): String {
        return if (path != null) {
            val names = path.split("/".toRegex()).toTypedArray()
            path.substring(0, path.length - names[names.size - 1].length)
        } else {
            ""
        }
    }

    private fun checkAlbum(
        albumImages: List<AlbumImage?>,
        localImage: LocalImage
    ): Boolean {
        var check = false
        for (i in albumImages.indices) {
            if (getAlbumName(localImage.path) == albumImages[i]?.name) {
                check = true
                albumImages[i]!!.getLocalImages()?.add(localImage)
                break
            }
            check = false
        }
        return check
    }
}
