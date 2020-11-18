package com.example.dat.democamera

import android.content.Context
import android.graphics.Color
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.makeramen.roundedimageview.RoundedImageView

class ImageChooseAdapter(
    private val mContext: Context,
    private val mPathImages: List<String>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var selectedItem = -1
  //  private var mOnClickItemListener: ImageChooseAdapterWithEmptyLayout.OnClickItemListener? = null
    private var mOnClickCamera: OnClickCamera? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        var view: View
        view = LayoutInflater.from(mContext).inflate(R.layout.image_choose_item, parent, false)
        return  ImageHolder(view)

    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (mPathImages.isEmpty()) return


//        if (getItemViewType(position) == TYPE_CAMERA) {
//        } else {
        if (holder is ImageHolder) {
            val pathItem = mPathImages[position]
            //            if (pathItem.equals("camera")) return;
            val imageHolder =
                holder

            //set background when it selected
            if (selectedItem == position) imageHolder.img.setBackgroundColor(
                Color.parseColor(
                    "#ffc715"
                )
            ) else imageHolder.img.setBackgroundColor(Color.TRANSPARENT)
            //set click event
            imageHolder.itemView.setOnClickListener { v: View? ->
                if (!canTouch()) return@setOnClickListener
                //set selected item
                selectedItem = position
                notifyDataSetChanged()
                //call back when press img with its position
                if (mOnClickCamera != null) mOnClickCamera!!.onClick(position)
            }
            //load image in to view
            try {
                Glide.with(mContext)
                    .load(pathItem).centerCrop().into(imageHolder.img)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

//        }
    }

    private var mLastClickTime: Long = 0
    fun canTouch(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }

    override fun getItemCount(): Int {
        return mPathImages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mPathImages[position] == "camera") TYPE_CAMERA else TYPE_IMAGE
    }

    internal inner class ImageHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var img: RoundedImageView

        init {
            img = itemView.findViewById(R.id.img)
        }
    }



    fun addOnClickItemListener(onClickItemListener: OnClickCamera?) {
      //  mOnClickItemListener = onClickItemListener
        mOnClickCamera = onClickItemListener
    }

    fun addOnClickCameraListener(onClickCamera: OnClickCamera?) {

    }

    interface OnClickItemListener {
        fun onClick(position: Int)
    }



    interface OnClickCamera {
        fun onClick(position: Int)
    }

    companion object {
        private const val TYPE_CAMERA = 1
        private const val TYPE_IMAGE = 2
    }

}