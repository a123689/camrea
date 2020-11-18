package com.example.dat.democamera

  class ImageFolder {
    var path: String? = null
    var folderName: String? = null
     var numberOfPics = 0
     var firstPic: String? = null

    constructor() {}
    constructor(path: String?, folderName: String?) {
        this.path = path
        this.folderName = folderName
    }

    fun addpics() {
        numberOfPics++
    }

}
