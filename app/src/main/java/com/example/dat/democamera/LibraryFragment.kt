package com.example.dat.democamera

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.security.Permission
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LibraryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LibraryFragment : Fragment() {
    var menu: PopupMenu? = null
    var folds: ArrayList<ImageFolder>? = null
    private val CAMERA_CODE = 1
    private val CAMERA_ACTIVITY_REQ = 2


    var tvFoder: TextView? = null


    var recyclerView: RecyclerView? = null


    lateinit var reLativeFoder: RelativeLayout
    private var mImageChooseAdapter: ImageChooseAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var mPathImages: MutableList<String>? = null
    private var mCurrentPhotoPath: String? = null

    fun MylibraryDialogFragment() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_main2, container, false)
    }

    private var listAllImage: List<String>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
        reLativeFoder = view.findViewById(R.id.RelativeForder)
        recyclerView = view.findViewById(R.id.rcv)
        tvFoder = view.findViewById(R.id.tvFoder)
        menu = PopupMenu(context, reLativeFoder)

//        folds = getPicturePaths();
        menu!!.menu.clear()
        menu!!.menu.add("Folder")
        //        for (ImageFolder i : folds) {
//            menu.getMenu().add(i.getFolderName());
//        }
        reLativeFoder!!.setOnClickListener {
            menu!!.show()
            menu!!.setOnMenuItemClickListener { menuItem ->
                tvFoder!!.text = menuItem.title
                if (context != null && context!!.resources != null && menuItem.title != null) {
                    if (menuItem.title == "Folder"
                    ) {
                        if (mPathImages != null) {
                            mPathImages!!.clear()
                           // mPathImages!!.add("camera")
                            if (listAllImage != null) {
                                mPathImages!!.addAll(listAllImage!!)
                            }
                            mImageChooseAdapter!!.notifyDataSetChanged()
                        }
                    } else {
                        for (folder in listFolderImage!!) {
                            if (folder.name.contentEquals(menuItem.title)) {
                                if (mPathImages != null) {
                                    mPathImages!!.clear()
                                 //   mPathImages!!.add("camera")
                                    for (item in folder.getLocalImages()!!) {
                                        mPathImages!!.add(item?.path!!)
                                    }
                                    mImageChooseAdapter!!.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
                true
            }
        }
        mLayoutManager = GridLayoutManager(context, 4)
        (mLayoutManager as GridLayoutManager).orientation = RecyclerView.VERTICAL
        mPathImages = mutableListOf()
        mPathImages!!.add("camera")
        mImageChooseAdapter = ImageChooseAdapter(context!!, mPathImages!!)
        mImageChooseAdapter!!.addOnClickItemListener(object : ImageChooseAdapter.OnClickCamera {
            override fun onClick(position: Int) {

                val pathImage =
                    mPathImages!!.get(position)

                if (listener != null) {
                    //listener!!.onClickImage(pathImage)
                    ImageUtils.path = pathImage
                    var cameraFragment = EditFragment()
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.container, cameraFragment)
                        ?.commit()
                }
            }
        })
        // if user choose position = 0 request camera to take picture by camera

        recyclerView!!.adapter = mImageChooseAdapter
        recyclerView!!.layoutManager = mLayoutManager

        //request read external memory permission, if it is granted then load all image
        getDummyFolder()
    }


    private val listOfFolderImage =
        ArrayList<String>()
    private val listOfAllImages =
        ArrayList<String>()
    private var listFolderImage: List<AlbumImage>? = null

    private fun getDummyFolder() {
        GlobalScope.launch (Dispatchers.IO){
            listFolderImage = ImageUtils.getAllImage(context!!) as List<AlbumImage>?
            listOfFolderImage.add("Folder")
            for (albumImage in listFolderImage!!) {
                menu!!.menu.add(albumImage.name)
                listOfFolderImage.add(albumImage.name)
                for (localImage in albumImage.getLocalImages()!!) {
                    listOfAllImages.add(localImage?.path!!)
                }
            }

            withContext(Dispatchers.Main){
                mPathImages!!.clear()
                // mPathImages!!.add("camera")
                mPathImages!!.addAll(listOfAllImages)
                listAllImage = listOfAllImages
                mImageChooseAdapter?.notifyDataSetChanged()
            }
        }

    }


    fun setListener2(listener: addItemClick?) {
        this.listener = listener
    }

    var listener: addItemClick? = null

    interface addItemClick {
        fun onClickImage(pathImage: String?)
    }

//    private fun loadImage() {
//        LoadImageUtils.getInstance(context)
//            .addOnLoadImageListener(object : OnLoadImageListener() {
//                fun onCompleted(pathImages: List<String>?) {
//                    FileUtils.sortByLastModified(pathImages)
//                    mPathImages!!.clear()
//                    mPathImages!!.add("camera")
//                    mPathImages!!.addAll(pathImages!!)
//                    listAllImage = pathImages
//                    mImageChooseAdapter.notifyDataSetChanged()
//                }
//
//                fun onFailure() {}
//            }).setTypeLoad(LoadImageUtils.TypeLoad.FORDER)
//            .load(AppConstant.MAIN_PATH, true)
//    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val imageFileName = "image_" + System.currentTimeMillis() + ".jpg"
        val storageDir =
            context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File(storageDir, imageFileName)
        val isOK = image.createNewFile()
        // Save a file: path for use with ACTION_VIEW intents
        if (isOK
            && image.absolutePath != null && !image.absolutePath.isEmpty()
        ) mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun goToCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context!!.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        val m =
                            StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                        m.invoke(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val photoURI = Uri.fromFile(photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, CAMERA_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAMERA_CODE -> if (resultCode == Activity.RESULT_OK) {
                if (mCurrentPhotoPath != null && !mCurrentPhotoPath!!.isEmpty()) {

                    if (listener != null) {
                        listener!!.onClickImage(mCurrentPhotoPath)
                    }
                }
            }

        }
    }



    private fun getPicturePaths(): ArrayList<ImageFolder>? {
        val picFolders: ArrayList<ImageFolder> = ArrayList<ImageFolder>()
        val picPaths = ArrayList<String>()
        val allImagesuri =
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID
        )
        val cursor =
            context!!.contentResolver.query(allImagesuri, projection, null, null, null)
        try {
            cursor?.moveToFirst()
            do {
                val folds = ImageFolder()
                val name =
                    cursor!!.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                val folder =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val datapath =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))

                //String folderpaths =  datapath.replace(name,"");
                var folderpaths =
                    datapath.substring(0, datapath.lastIndexOf("$folder/"))
                folderpaths = "$folderpaths$folder/"
                if (!picPaths.contains(folderpaths)) {
                    picPaths.add(folderpaths)
                    folds.path = folderpaths
                    folds.folderName = folder
                    folds.firstPic = datapath //if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                    folds.addpics()
                    picFolders.add(folds)
                } else {
                    for (i in picFolders.indices) {
                        if (picFolders[i].path.equals(folderpaths)) {
                            picFolders[i].firstPic = datapath
                            picFolders[i].addpics()
                        }
                    }
                }
            } while (cursor!!.moveToNext())
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        for (i in picFolders.indices) {
            Log.d(
                "picture folders",
                picFolders[i].folderName.toString() + " and path = " + picFolders[i]
                    .path + " " + picFolders[i].numberOfPics
            )
        }
        return picFolders
    }

//    fun getAllImagesByFolder(path: String): ArrayList<PictureFacer?>? {
//        var images: ArrayList<PictureFacer?> = ArrayList<PictureFacer?>()
//        val allVideosuri =
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        val projection = arrayOf(
//            MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
//            MediaStore.Images.Media.SIZE
//        )
//        val cursor = context!!.contentResolver.query(
//            allVideosuri,
//            projection,
//            MediaStore.Images.Media.DATA + " like ? ",
//            arrayOf("%$path%"),
//            null
//        )
//        try {
//            cursor!!.moveToFirst()
//            do {
//                val pic = PictureFacer()
//                pic.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)))
//                pic.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)))
//                pic.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)))
//                images.add(pic)
//            } while (cursor.moveToNext())
//            cursor.close()
//            val reSelection: ArrayList<PictureFacer?> =
//                ArrayList<PictureFacer?>()
//            for (i in images.size - 1 downTo -1 + 1) {
//                reSelection.add(images[i])
//            }
//            images = reSelection
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return images
//    }
//
//    fun getPathForderByName(name: String?): String? {
//        for (i in folds!!) {
//            if (i.getFolderName().equals(name)) {
//                return i.getPath()
//            }
//        }
//        return "null"
//    }

    override fun onStart() {
        super.onStart()
//        val d: Dialog = getDialog()
//        if (d != null) {
//            val width = ViewGroup.LayoutParams.MATCH_PARENT
//            val height = ViewGroup.LayoutParams.MATCH_PARENT
//            d.window!!.setLayout(width, height)
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog_theme)
    }
}