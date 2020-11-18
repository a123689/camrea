package com.example.dat.democamera

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

class LocalImage : Parcelable {
    var uri: Uri? = null
    var path: String? = null
    var name: String? = null
    var isSelect = false

    protected constructor(`in`: Parcel) {
        uri = `in`.readParcelable(Uri::class.java.classLoader)
        path = `in`.readString()
        name = `in`.readString()
        isSelect = `in`.readByte().toInt() != 0
    }

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(uri, flags)
        dest.writeString(path)
        dest.writeString(name)
        dest.writeByte((if (isSelect) 1 else 0).toByte())
    }

    companion object {
        val CREATOR: Parcelable.Creator<LocalImage?> = object : Parcelable.Creator<LocalImage?> {
            override fun createFromParcel(`in`: Parcel): LocalImage? {
                return LocalImage(`in`)
            }

            override fun newArray(size: Int): Array<LocalImage?> {
                return arrayOfNulls(size)
            }
        }
    }
}