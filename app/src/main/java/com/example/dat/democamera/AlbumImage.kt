package com.example.dat.democamera

import java.util.ArrayList

class AlbumImage(
    localImages: MutableList<LocalImage?>?,
    name: String
) {
    private val localImages: MutableList<LocalImage?>?
    var name: String
    var path: String? = null

    fun getLocalImages(): MutableList<LocalImage?>? {
        return localImages
    }

    val firstImage: String?
        get() = if (localImages != null && !localImages.isEmpty() && localImages[0] != null) {
            localImages[0]?.path
        } else null

    init {
        this.localImages = localImages
        this.name = name
    }
}